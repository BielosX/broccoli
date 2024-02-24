plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "broccoli"
version = "1.0.0"

gradlePlugin {
    val javaPmdPlugin by plugins.creating {
        id = "broccoli.java-pmd-plugin"
        implementationClass = "broccoli.JavaPmdPlugin"
    }
}