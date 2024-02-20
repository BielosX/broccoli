import broccoli.JooqPostgresGeneratorPluginExtension

plugins {
    id("java-library")
    id("maven-publish")
    id("broccoli.jooq-postgres-generator-plugin") version "1.0.0"
}

configure<JooqPostgresGeneratorPluginExtension> {
    packageName = "broccoli.jooq"
    outputDirectory = project.layout.buildDirectory.dir("jooqGenerated")
    flywaySqlDirectory = project.layout.projectDirectory.dir("migrations")
    postgresVersion = "16.2"
}