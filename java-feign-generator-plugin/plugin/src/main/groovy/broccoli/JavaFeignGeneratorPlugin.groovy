package broccoli

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

interface JavaFeignGeneratorPluginExtension {
    Property<String> getApiPackage()
    Property<String> getModelPackage()
    Property<String> getInputSpec()
    Property<String> getOutputDir()
}

class JavaFeignGeneratorPlugin implements Plugin<Project> {
    private final ClassLoader classLoader = getClass().getClassLoader()

    void apply(Project project) {
        def extension = project.extensions.create('javaFeignGenerator',
                JavaFeignGeneratorPluginExtension)
        def templatesDir = project.layout.buildDirectory.dir('templates').get()
        def copyTemplatesTask = project.tasks.register('copyMustacheTemplates').get()
        copyTemplatesTask.doFirst {
            project.mkdir(templatesDir)
            ['model.mustache', 'pojo.mustache'].each {
                def resource = classLoader.getResourceAsStream(it)
                templatesDir.file(it).asFile.setBytes(resource.readAllBytes())
            }
        }
        def generateTask = project.tasks.register('generateJavaFeignClient', GenerateTask.class)
        generateTask.configure {
            it.dependsOn(copyTemplatesTask)
            def apiPackage = extension.apiPackage.get()
            def modelPackage = extension.modelPackage.get()
            def inputSpecification = extension.inputSpec.get()
            def outputDirectory = extension.outputDir.get()
            it.generatorName.set('java')
            it.inputSpec.set(inputSpecification)
            it.outputDir.set(outputDirectory)
            it.templateDir.set(templatesDir.asFile.path)
            it.configOptions.set([
                    dateLibrary: 'java8',
                    library: 'feign',
                    hideGenerationTimestamp: 'true',
                    useJakartaEe: 'true',
                    apiPackage: apiPackage,
                    modelPackage: modelPackage,
                    serializationLibrary: 'jackson'
            ])
            it.globalProperties.set([
                    apis: "",
                    models: "",
                    supportingFiles: ""
            ])
        }
    }
}
