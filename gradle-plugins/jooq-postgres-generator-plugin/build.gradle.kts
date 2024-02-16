plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

version = "1.0.0"
group = "broccoli"

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