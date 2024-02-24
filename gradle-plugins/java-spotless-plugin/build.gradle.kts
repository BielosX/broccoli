plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "broccoli"
version = "1.0.0"

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
}

gradlePlugin {
    val javaSpotlessPlugin by plugins.creating {
        id = "broccoli.java-spotless-plugin"
        implementationClass = "broccoli.JavaSpotlessPlugin"
    }
}