import java.net.URI

plugins {
    `java-gradle-plugin`
    `maven-publish`
    `kotlin-dsl`
    alias(libs.plugins.jvm)
}

group = "broccoli"
version = "1.0.0"

val repositoryUrl = project.property("repositoryUrl")
val repositoryPassword = project.property("repositoryPassword")
val repositoryUser = project.property("repositoryUser")

repositories {
    mavenCentral()
}

gradlePlugin {
    val logger by plugins.creating {
        id = "broccoli.logger-plugin"
        implementationClass = "broccoli.LoggerPlugin"
    }
}

publishing {
    repositories {
        maven {
            url = URI.create(repositoryUrl.toString())
            credentials {
                username = repositoryUser.toString()
                password = repositoryPassword.toString()
            }
        }
    }
}