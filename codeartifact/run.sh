#!/bin/bash -e

export AWS_PAGER=""
AWS_REGION="eu-west-1"
export AWS_REGION
STACK_NAME="code-artifact"

POSITIONAL_ARGS=()
number_of_args="$#"
while (( number_of_args > 0 )); do
  case "$1" in
    "--region")
      AWS_REGION="$2"
      export AWS_REGION
      shift
      shift
      ;;
    *)
      POSITIONAL_ARGS+=("$1")
      shift
  esac
  number_of_args="$#"
done

set -- "${POSITIONAL_ARGS[@]}" # restore positional parameters

function delete_stack() {
  echo "Deleting stack $1"
  aws cloudformation delete-stack --stack-name "$1"
  while aws cloudformation describe-stacks --stack-name "$1" &> /dev/null; do
    echo "Stack $1 still exists. Waiting"
    sleep 10
  done
  echo "Stack $1 deleted"
}

function deploy() {
  aws cloudformation deploy --stack-name "${STACK_NAME}" --template-file main.yaml
}

function destroy() {
  delete_stack "${STACK_NAME}"
}

function gradle_properties() {
  token=$(aws codeartifact get-authorization-token \
    --domain broccoli --duration-seconds 43200 | jq -r '.authorizationToken')
  account_id=$(aws sts get-caller-identity | jq -r '.Account')
  url="https://broccoli-${account_id}.d.codeartifact.${AWS_REGION}.amazonaws.com/maven/maven-custom/"
read -r -d '\0' properties << EOM
repositoryUrl=$url
repositoryUser=aws
repositoryPassword=$token
\0
EOM
  if [ -f ~/.gradle/gradle.properties ]; then
    cp ~/.gradle/gradle.properties ~/.gradle/gradle.properties.backup
    echo "Created backup of ~/.gradle/gradle.properties as ~/.gradle/gradle.properties.backup"
  fi
  echo "$properties" > ~/.gradle/gradle.properties
}

function purge() {
  local result
  local next_repositories_token="null"
  local first_page=true
  while [ "${next_repositories_token}" != "null" ] || [ "${first_page}" = true ]; do
    first_page=false
    if [ "${next_repositories_token}" != "null" ]; then
      result=$(aws codeartifact list-repositories-in-domain --domain broccoli --starting-token "${next_repositories_token}")
    else
      result=$(aws codeartifact list-repositories-in-domain --domain broccoli)
    fi
    local repositories
    repositories=$(jq -r '.repositories' <<< "$result")
    local repos_length
    repos_length=$(jq -r 'length' <<< "$repositories")
    next_repositories_token=$(jq -r '.nextToken' <<< "$result")
    if ((repos_length > 0)); then
      for repo_idx in $(seq 0 $((repos_length-1))); do
        local repo_name
        local next_packages_token="null"
        local first_packages_page=true
        repo_name=$(jq -r ".[$repo_idx].name" <<< "$repositories")
        while [ "${next_packages_token}" != "null" ] || [ "${first_packages_page}" = true ]; do
          first_packages_page=false
          if [ "${next_packages_token}" != "null" ]; then
            result=$(aws codeartifact list-packages --domain broccoli --repository "${repo_name}" --starting-token "${next_packages_token}")
          else
            result=$(aws codeartifact list-packages --domain broccoli --repository "${repo_name}")
          fi
          next_packages_token=$(jq -r '.nextToken' <<< "$result")
          local packages_length
          local packages
          packages=$(jq -r '.packages' <<< "$result")
          packages_length=$(jq -r 'length' <<< "$packages")
          if ((packages_length > 0)); then
            for package_idx in $(seq 0 $((packages_length-1))); do
              local package_name
              local package_format
              package_name=$(jq -r ".[$package_idx].package" <<< "$packages")
              package_format=$(jq -r ".[$package_idx].format" <<< "$packages")
              package_namespace=$(jq -r ".[$package_idx].namespace" <<< "$packages")
              aws codeartifact delete-package \
                --domain broccoli --repository "${repo_name}" \
                --package "${package_name}" \
                --namespace "${package_namespace}" \
                --format "${package_format}" > /dev/null
              echo "${package_name} deleted from ${repo_name}"
            done
          fi
        done
      done
    fi
  done
}

case "$1" in
  "deploy") deploy ;;
  "destroy") destroy ;;
  "gradle-properties") gradle_properties ;;
  "purge") purge ;;
esac