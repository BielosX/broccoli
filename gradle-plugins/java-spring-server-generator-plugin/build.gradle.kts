plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "broccoli"
version = "1.0.0"

dependencies {
    implementation("org.openapitools:openapi-generator-gradle-plugin:7.2.0")
}

gradlePlugin {
    val javaSpringServerGenerator by plugins.creating {
        id = "broccoli.java-spring-server-generator-plugin"
        implementationClass = "broccoli.JavaSpringServerGeneratorPlugin"
    }
}
