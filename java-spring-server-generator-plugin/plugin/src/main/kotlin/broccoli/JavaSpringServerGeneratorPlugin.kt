package broccoli

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.provider.Property
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

interface JavaSpringServerGeneratorPluginExtension {
    val inputSpec: Property<String>
    val outputDir: Property<String>
    val apiPackage: Property<String>
    val modelPackage: Property<String>
}

class JavaSpringServerGeneratorPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("javaSpringServerGenerator",
                JavaSpringServerGeneratorPluginExtension::class.java)
        project.tasks.register("javaSpringApiGenerate", GenerateTask::class.java) { task ->
            task.generatorName.set("spring")
            task.inputSpec.set(extension.inputSpec.get())
            task.outputDir.set(extension.outputDir.get())
            task.configOptions.set(mapOf(
                    "apiPackage" to extension.apiPackage.get(),
                    "modelPackage" to extension.modelPackage.get(),
                    "dateLibrary" to "java8",
                    "library" to "spring-boot",
                    "hideGenerationTimestamp" to "true",
                    "useOptional" to "true",
                    "useSpringBoot3" to "true",
                    "interfaceOnly" to "true"
            ))
            task.globalProperties.set(mapOf(
                    "apis" to "",
                    "models" to "",
                    "supportingFiles" to ""
            ))
        }
    }
}
