import java.net.URI

plugins {
    `java-gradle-plugin`
    `maven-publish`
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
        isAutomatedPublishing = false
    }
}

publishing {
    publications {
        create<MavenPublication>("gradlePlugin") {
            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()

            from(components["java"])
        }
    }

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