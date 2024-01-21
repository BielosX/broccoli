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

case "$1" in
  "deploy") deploy ;;
  "destroy") destroy ;;
  "gradle-properties") gradle_properties ;;
esac