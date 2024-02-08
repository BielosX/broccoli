package broccoli

import org.gradle.api.Plugin
import org.gradle.api.Project

class LoggerPlugin: Plugin<Project> {
    companion object {
        const val SLF4J_API_VERSION = "2.0.11"
        const val SLF4J_RELOAD4J_VERSION = "2.0.11"
        const val IMPLEMENTATION = "implementation"
    }
    override fun apply(project: Project) {
        val log4jProps = javaClass.classLoader.getResourceAsStream("log4j.properties")?.readAllBytes()
        project.resources.text.fromString(log4jProps.toString())
        project.dependencies.apply {
            add(IMPLEMENTATION, create("org.slf4j:slf4j-api:${SLF4J_API_VERSION}"))
            add(IMPLEMENTATION, create("rg.slf4j:slf4j-reload4j:${SLF4J_RELOAD4J_VERSION}"))
        }
        project.configurations.forEach {
            it.exclude(mapOf(
                    "group" to "ch.qos.logback",
                    "module" to "logback-classic"
            ))
            it.exclude(mapOf(
                    "group" to "org.apache.logging.log4j",
                    "module" to "log4j-to-slf4j"
            ))
        }
    }
}
