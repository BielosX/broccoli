plugins {
    `kotlin-dsl`
    `maven-publish`
}

val repositoryUrl = project.property("repositoryUrl")
val repositoryPassword = project.property("repositoryPassword")
val repositoryUser = project.property("repositoryUser")

tasks.getByName<Jar>("jar") {
    enabled = false
}

allprojects {
    repositories {
        maven {
            url = uri(repositoryUrl!!)
            credentials {
                username = repositoryUser.toString()
                password = repositoryPassword.toString()
            }
        }
    }
}

subprojects {
    apply(plugin = "maven-publish")

    publishing {
        repositories {
            maven {
                url = uri(repositoryUrl!!)
                credentials {
                    username = repositoryUser.toString()
                    password = repositoryPassword.toString()
                }
            }

        }
    }
}