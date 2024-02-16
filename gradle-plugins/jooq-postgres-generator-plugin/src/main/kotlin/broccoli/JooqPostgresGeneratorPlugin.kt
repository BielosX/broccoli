package broccoli

import com.bmuschko.gradle.docker.DockerRemoteApiPlugin
import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerKillContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import nu.studer.gradle.jooq.JooqExtension
import nu.studer.gradle.jooq.JooqGenerate
import nu.studer.gradle.jooq.JooqPlugin
import org.flywaydb.gradle.FlywayExtension
import org.flywaydb.gradle.FlywayPlugin
import org.flywaydb.gradle.task.FlywayMigrateTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.jooq.meta.jaxb.Logging

interface JooqPostgresGeneratorPluginExtension {
    val postgresVersion: Property<String>
    val packageName: Property<String>
    val outputDirectory: Property<String>
    val flywaySqlDirectory: Property<String>
}

class JooqPostgresGeneratorPlugin: Plugin<Project> {
    companion object {
        const val POSTGRES_DRIVER_VERSION = "42.7.1"
    }
    override fun apply(project: Project) {
        val extension = project.extensions.create("jooqPostgresGenerate",
            JooqPostgresGeneratorPluginExtension::class.java)
        project.plugins.apply(JooqPlugin::class.java)
        project.plugins.apply(FlywayPlugin::class.java)
        project.plugins.apply(DockerRemoteApiPlugin::class.java)
        project.dependencies.add("jooqGenerator",
            "org.postgresql:postgresql:${POSTGRES_DRIVER_VERSION}")
        project.afterEvaluate {
            val imageTag = "${extension.postgresVersion.get()}-alpine"
            val migrationsDir = extension.flywaySqlDirectory.get()
            val postgresPort = "5432"
            val postgresUser = "jooq"
            val jdbcUrl = "jdbc:postgresql://localhost:${postgresPort}/${postgresUser}"
            val postgresDriver = "org.postgresql.Driver"
            project.extensions.configure(JooqExtension::class.java) {
                configurations.create("main") {
                    jooqConfiguration.apply {
                        logging = Logging.WARN
                        jdbc.apply {
                            driver = postgresDriver
                            url = jdbcUrl
                            user = postgresUser
                            password = postgresUser
                        }
                        generator.apply {
                            name = "org.jooq.codegen.DefaultGenerator"
                            strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                            database.apply {
                                name = "org.jooq.meta.postgres.PostgresDatabase"
                                inputSchema = "public"
                            }
                            generate.apply {
                                isDeprecated = false
                                isRecords = true
                                isImmutablePojos = true
                                isFluentSetters = true
                            }
                            target.apply {
                                packageName = extension.packageName.get()
                                directory = extension.outputDirectory.get()
                            }
                        }
                    }
                }
            }
            project.extensions.configure(FlywayExtension::class.java) {
                password = postgresUser
                user = postgresUser
                url = jdbcUrl
                driver = postgresDriver
                schemas = arrayOf("public")
                locations = arrayOf("filesystem:${migrationsDir}")
            }
            val createContainer = project.tasks.register("createPostgresContainer",
                DockerCreateContainer::class.java) {
                imageId.set("postgres:${imageTag}")
                hostConfig.autoRemove.set(true)
                hostConfig.portBindings.set(listOf("${postgresPort}:${postgresPort}"))
                hostConfig.tmpFs.set(mapOf(
                    "/var/lib/postgresql/data" to "rw,noexec,size=500m" // Read/Write, Non-Executable, 500MB
                ))
                envVars.set(mapOf(
                    "POSTGRES_USER" to postgresUser,
                    "POSTGRES_PASSWORD" to postgresUser,
                    "POSTGRES_DB" to postgresUser
                ))
                doLast {
                    println("Created Postgres container with ID $containerId")
                }
            }
            val startContainer = project.tasks.register("startPostgresContainer",
                DockerStartContainer::class.java) {
                dependsOn(createContainer)
                containerId.set(createContainer.get().containerId)
                doLast {
                    println("Started Postgres container with ID $containerId")
                }
            }
            val migrateDatabase = project.tasks.withType(FlywayMigrateTask::class.java)
            migrateDatabase.configureEach {
                dependsOn(startContainer)
                connectRetries = 10
                connectRetriesInterval = 30
            }
            val killContainer = project.tasks.register("killPostgresContainer",
                DockerKillContainer::class.java) {
                containerId.set(createContainer.get().containerId)
            }
            project.tasks.withType(JooqGenerate::class.java) {
                dependsOn(migrateDatabase)
                finalizedBy(killContainer)
            }
        }
    }
}
