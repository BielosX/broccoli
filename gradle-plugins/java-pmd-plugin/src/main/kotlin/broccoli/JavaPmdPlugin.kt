package broccoli

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.PmdExtension
import org.gradle.api.plugins.quality.PmdPlugin

class JavaPmdPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(PmdPlugin::class.java)
        val resource = String(javaClass.classLoader.getResourceAsStream("custom-rules.xml")!!.readAllBytes())
        val rules = project.resources.text.fromString(resource)
        project.extensions.configure(PmdExtension::class.java) {
            ruleSetFiles(rules)
        }
    }
}