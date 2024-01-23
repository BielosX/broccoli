package broccoli

import org.gradle.api.Project
import org.gradle.api.Plugin

import java.nio.charset.StandardCharsets

class LoggerPlugin implements Plugin<Project> {
    private dependencies = [
            'org.slf4j:slf4j-api:2.0.11',
            'org.slf4j:slf4j-reload4j:2.0.11'
    ]
    void apply(Project project) {
        dependencies.each {
            project.dependencies.add('implementation', it)
        }
        def log4jProps = new String(getClass().getClassLoader().getResourceAsStream('log4j.properties').readAllBytes(),
                StandardCharsets.UTF_8)
        project.resources.text.fromString(log4jProps)
        project.configurations.configureEach {
            exclude group: 'ch.qos.logback', module: 'logback-classic'
            exclude group: 'org.apache.logging.log4j', module: 'log4j-to-slf4j'
        }
    }
}
