package broccoli

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

class LoggerPlugin: Plugin<Project> {
    companion object {
        const val SLF4J_API_VERSION = "2.0.11"
        const val SLF4J_RELOAD4J_VERSION = "2.0.11"
        const val LOG4J_PROPERTIES_FILE = "log4j.properties"
    }
    override fun apply(project: Project) {
        val log4jProps = javaClass.classLoader.getResourceAsStream(LOG4J_PROPERTIES_FILE)?.readAllBytes()
        project.resources.text.fromString(log4jProps.toString())
        project.dependencies {
            "implementation"("org.slf4j:slf4j-api:${SLF4J_API_VERSION}")
            "implementation"("org.slf4j:slf4j-reload4j:${SLF4J_RELOAD4J_VERSION}")
        }
        project.configurations.forEach {
            it.exclude(group = "ch.qos.logback", module = "logback-classic")
            it.exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
        }
    }
}
