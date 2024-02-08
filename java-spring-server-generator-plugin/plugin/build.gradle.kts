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

dependencies {
    implementation("org.openapitools:openapi-generator-gradle-plugin:7.2.0")
}

gradlePlugin {
    val javaSpringServerGenerator by plugins.creating {
        id = "broccoli.${rootProject.name}"
        implementationClass = "broccoli.JavaSpringServerGeneratorPlugin"
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
