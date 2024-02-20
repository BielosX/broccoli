package broccoli

import org.flywaydb.core.Flyway
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileType
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.*
import org.jooq.meta.jaxb.Target
import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

abstract class JooqPostgresGenerateTask: DefaultTask() {
    @get:Input
    abstract val postgresVersion: Property<String>
    @get:Input
    abstract val packageName: Property<String>

    @get:Incremental
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:InputDirectory
    abstract val migrationSql: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate(inputChanges: InputChanges) {
        val changedFiles = inputChanges.getFileChanges(migrationSql).filter {
            it.fileType != FileType.DIRECTORY
        }.size
        if (changedFiles > 0) {
            val postgresImage = DockerImageName.parse("postgres:${postgresVersion.get()}-alpine")
            val postgresContainer = PostgreSQLContainer(postgresImage)
            postgresContainer.withTmpFs(
                mapOf(
                    "/var/lib/postgresql/data" to "rw,noexec,size=500m" // Read/Write, Non-Executable, 500MB
                )
            )
            postgresContainer.start()
            val dataSource = PGSimpleDataSource()
            dataSource.password = postgresContainer.password
            dataSource.user = postgresContainer.username
            dataSource.databaseName = postgresContainer.databaseName
            dataSource.serverNames = arrayOf(postgresContainer.host)
            dataSource.portNumbers = intArrayOf(postgresContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT))
            val migrationsLocation = "filesystem:${migrationSql.get().asFile.path}"
            println("Using migrations located at $migrationsLocation")
            val flyway = Flyway.configure()
                .connectRetries(10)
                .connectRetriesInterval(30)
                .locations(migrationsLocation)
                .dataSource(dataSource)
                .load()
            flyway.migrate()
            val jooqConfiguration = Configuration()
                .withJdbc(Jdbc()
                    .withDriver("org.postgresql.Driver")
                    .withUser(postgresContainer.username)
                    .withPassword(postgresContainer.password)
                    .withUrl(postgresContainer.jdbcUrl))
                .withGenerator(Generator()
                    .withName("org.jooq.codegen.DefaultGenerator")
                    .withStrategy(Strategy().withName("org.jooq.codegen.DefaultGeneratorStrategy"))
                    .withDatabase(Database()
                        .withName("org.jooq.meta.postgres.PostgresDatabase")
                        .withInputSchema("public"))
                    .withGenerate(Generate()
                        .withDeprecated(false)
                        .withRecords(true)
                        .withImmutablePojos(true)
                        .withFluentSetters(true))
                    .withTarget(Target()
                        .withPackageName(packageName.get())
                        .withDirectory(outputDir.get().asFile.path)))
            GenerationTool.generate(jooqConfiguration)
            postgresContainer.stop()
        }
    }
}