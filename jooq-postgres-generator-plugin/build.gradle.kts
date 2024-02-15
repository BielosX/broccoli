import java.net.URI

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    alias(libs.plugins.jvm)
}

version = "1.0.0"
group = "broccoli"

val repositoryUrl = project.property("repositoryUrl")
val repositoryPassword = project.property("repositoryPassword")
val repositoryUser = project.property("repositoryUser")

repositories {
    gradlePluginPortal()
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation("com.bmuschko:gradle-docker-plugin:9.4.0")
    implementation("nu.studer:gradle-jooq-plugin:9.0")
    implementation("gradle.plugin.org.flywaydb:gradle-plugin-publishing:10.7.2")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.7.2")
    runtimeOnly("org.postgresql:postgresql:42.7.1")
}

gradlePlugin {
    val jooqPostgresGenerator by plugins.creating {
        id = "broccoli.jooq-postgres-generator-plugin"
        implementationClass = "broccoli.JooqPostgresGeneratorPlugin"
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