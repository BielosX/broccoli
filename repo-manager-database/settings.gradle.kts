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
rootProject.name = "repo-manager-database"
