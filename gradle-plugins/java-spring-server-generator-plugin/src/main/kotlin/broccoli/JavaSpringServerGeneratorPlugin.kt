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
        val jarLocation = javaClass.protectionDomain.codeSource.location.path
        val templatesDir = project.layout.buildDirectory.dir("templates").get()
        val copyTask = project.tasks.register("copyMustacheTemplates").get()
        val mustacheFiles = project.zipTree(jarLocation).matching {
            include("*.mustache")
        }
        copyTask.doFirst {
            project.mkdir(templatesDir)
            project.copy {
                from(mustacheFiles)
                into(templatesDir)
            }
        }
        project.tasks.register("javaSpringApiGenerate", GenerateTask::class.java) {
            dependsOn(copyTask)
            generatorName.set("spring")
            inputSpec.set(extension.inputSpec.get())
            outputDir.set(extension.outputDir.get())
            templateDir.set(templatesDir.asFile.path)
            configOptions.set(mapOf(
                "apiPackage" to extension.apiPackage.get(),
                "modelPackage" to extension.modelPackage.get(),
                "dateLibrary" to "java8",
                "library" to "spring-boot",
                "hideGenerationTimestamp" to "true",
                "useOptional" to "",
                "useSpringBoot3" to "true",
                "interfaceOnly" to "true",
                "openApiNullable" to ""
            ))
            globalProperties.set(mapOf(
                "apis" to "",
                "models" to "",
                "supportingFiles" to ""
            ))
        }
    }
}
