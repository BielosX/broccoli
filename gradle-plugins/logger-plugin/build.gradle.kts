plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "broccoli"
version = "1.0.0"

gradlePlugin {
    val logger by plugins.creating {
        id = "broccoli.logger-plugin"
        implementationClass = "broccoli.LoggerPlugin"
    }
}