plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

version = "1.0.0"
group = "broccoli"

dependencies {
    implementation("org.jooq:jooq:3.19.3")
    implementation("org.jooq:jooq-meta:3.19.3")
    implementation("org.jooq:jooq-codegen:3.19.3")
    implementation("org.flywaydb:flyway-core:10.8.1")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.8.1")
    implementation("org.testcontainers:postgresql:1.19.5")
    implementation("org.postgresql:postgresql:42.7.1")

}

gradlePlugin {
    val jooqPostgresGenerator by plugins.creating {
        id = "broccoli.jooq-postgres-generator-plugin"
        implementationClass = "broccoli.JooqPostgresGeneratorPlugin"
    }
}