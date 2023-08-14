package com.poksha.sample.infrastructure.api

import cats.effect._
import com.comcast.ip4s._
import com.poksha.sample.application.auth.AuthServiceImpl
import com.poksha.sample.domain.auth.AuthUserRepository
import com.poksha.sample.infrastructure.api.config.AppConfig
import com.poksha.sample.infrastructure.api.v1.routes.V1Routes
import com.poksha.sample.infrastructure.database.postgres.{AuthUserRepositoryPostgres, PostgresConfig}
import com.typesafe.config.ConfigFactory
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router

import scala.util.chaining.scalaUtilChainingOps

object Main extends IOApp {
  private val rootService = HttpRoutes.of[IO] { case GET -> Root =>
    Ok(s"Sample API")
  }

  private val dbConfig = ConfigFactory
    .load()
    .pipe(config =>
      PostgresConfig(
        url = config.getString("database.postgres.url"),
        user = config.getString("database.postgres.user"),
        pass = config.getString("database.postgres.pass")
      )
    )

  implicit val authUserRepository: AuthUserRepository =
    AuthUserRepositoryPostgres(dbConfig)

  private val appConfig = ConfigFactory
    .load()
    .pipe(config =>
      AppConfig(
        port = config.getInt("application.server.port"),
        host = config.getString("application.server.host")
      )
    )

  val authService = new AuthServiceImpl
  private val v1Routes = V1Routes.routes(authService)
  private val httpApp = Router("/" -> rootService, "/v1" -> v1Routes).orNotFound
  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(
        Host.fromString(appConfig.host).getOrElse(throw new Exception("Invalid host"))
      )
      .withPort(
        Port.fromInt(appConfig.port).getOrElse(throw new Exception("Invalid port"))
      )
      .withHttpApp(httpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
