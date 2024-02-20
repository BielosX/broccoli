package broccoli

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

interface JooqPostgresGeneratorPluginExtension {
    val postgresVersion: Property<String>
    val packageName: Property<String>
    val outputDirectory: DirectoryProperty
    val flywaySqlDirectory: DirectoryProperty
}

class JooqPostgresGeneratorPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("jooqPostgresGenerate",
            JooqPostgresGeneratorPluginExtension::class.java)
        project.afterEvaluate {
            project.tasks.register("jooqPostgresGenerate", JooqPostgresGenerateTask::class.java) {
                postgresVersion.set(extension.postgresVersion.get())
                packageName.set(extension.packageName.get())
                migrationSql.set(extension.flywaySqlDirectory.get())
                outputDir.set(extension.outputDirectory.get())
            }
        }
    }
}
