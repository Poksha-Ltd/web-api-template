package com.poksha.sample.infrastructure.database.postgres

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.poksha.sample.infrastructure.database.postgres.RepositoryPostgresTestBase._
import doobie.implicits._
import doobie.util.fragment.Fragment
import doobie.util.transactor
import doobie.util.transactor.Transactor
import org.slf4j.LoggerFactory
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

import java.io.File
import java.nio.file.{Files, Paths}

trait RepositoryPostgresTestBase {
  private[this] val logger = LoggerFactory.getLogger(getClass)

  var testConfig: PostgresConfig = _
  var testTransActor: transactor.Transactor.Aux[IO, Unit] = _

  final val postgresContainer: PostgreSQLContainer[_] = {
    val container = new PostgreSQLContainer(
      DockerImageName.parse(ImageTag)
    )
    container.withUsername(Username)
    container.withPassword(Password)
    container.withDatabaseName(DatabaseName)
    container
  }

  def setUp(): Unit = {
    logger.info(s"Test container and config setup starts")
    postgresContainer.start()
    postgresContainer.waitingFor(Wait.forHealthcheck())

    testConfig = PostgresConfig(
      url = postgresContainer.getJdbcUrl,
      user = postgresContainer.getUsername,
      pass = postgresContainer.getPassword
    )

    testTransActor = Transactor.fromDriverManager[IO](
      driver = testConfig.driver,
      url = testConfig.url,
      user = testConfig.user,
      pass = testConfig.pass
    )

    migrate(testTransActor)

    logger.info(s"Test container and config setup ended")
  }

  private def migrate(transActor: transactor.Transactor.Aux[IO, Unit]): Unit = {
    migrationFilePaths.foreach(path => {
      val migrationSql = Files.readString(Paths.get(path))
      logger.info(s"Migration file $path")
      logger.info(s"$migrationSql")
      Fragment
        .const(migrationSql)
        .update
        .run
        .transact(transActor)
        .unsafeRunSync()
    })
  }

  def cleanUp(): Unit = {
    logger.info(s"Test container clean up")
    postgresContainer.stop()
  }
}
object RepositoryPostgresTestBase {
  final val ImageTag = "postgres:15-alpine"
  final val Username = "postgres"
  final val Password = "password"
  final val DatabaseName = "authentication"

  final val migrationFilePaths: List[String] = {
    val migrationFileDir = new File("docker/migrate/version")
    val migrationFiles = migrationFileDir
      .listFiles()
      .filter(_.isFile)
      .filter(_.getName.endsWith("up.sql"))
      .map(_.getPath)
      .toList
      .sorted
    assert(migrationFiles.nonEmpty)
    migrationFiles
  }
}
