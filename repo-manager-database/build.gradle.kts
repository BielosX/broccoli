import broccoli.JooqPostgresGeneratorPluginExtension

plugins {
    id("java-library")
    id("maven-publish")
    id("broccoli.jooq-postgres-generator-plugin") version "1.0.0"
}

group = "broccoli"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configure<JooqPostgresGeneratorPluginExtension> {
    packageName = "broccoli.jooq"
    outputDirectory = "${projectDir}/build/jooqGenerated"
    flywaySqlDirectory = "${projectDir}/migrations"
    postgresVersion = "16.2"
}