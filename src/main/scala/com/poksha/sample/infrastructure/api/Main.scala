package com.poksha.sample.infrastructure.api

import cats.effect._
import com.comcast.ip4s._
import com.poksha.sample.application.auth.AuthService
import com.poksha.sample.infrastructure.api.v1.routes.V1Routes
import com.poksha.sample.infrastructure.database.inMemory.AuthUserRepositoryImpl
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.ember.server._
import org.http4s.implicits._
import org.http4s.server.Router

object Main extends IOApp {
  private val rootService = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok(s"Sample API")
  }
  val authService = new AuthService()(AuthUserRepositoryImpl)
  private val v1Routes = V1Routes.routes(authService)(AuthUserRepositoryImpl)
  private val httpApp = Router("/" -> rootService, "/v1" -> v1Routes).orNotFound
  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder.default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(httpApp)
        .build
        .use(_ => IO.never)
        .as(ExitCode.Success)
}
