package broccoli

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class JavaSpotlessPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply(SpotlessPlugin::class.java)
        project.extensions.configure(SpotlessExtension::class.java) {
            java {
                formatAnnotations()
                googleJavaFormat("17")
                    .reflowLongStrings()
                    .reorderImports(true)
                    .formatJavadoc(true)
            }
        }
    }
}