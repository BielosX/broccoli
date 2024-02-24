pluginManagement {
    val repositoryUrl: String by settings
    val repositoryUser: String by settings
    val repositoryPassword: String by settings
    repositories {
        maven {
            url = uri(repositoryUrl)
            credentials {
                username = repositoryUser
                password = repositoryPassword
            }
        }
    }
}
rootProject.name = "gradle-plugins"
include("logger-plugin")
include("java-feign-generator-plugin")
include("java-spring-server-generator-plugin")
include("jooq-postgres-generator-plugin")
include("java-pmd-plugin")
