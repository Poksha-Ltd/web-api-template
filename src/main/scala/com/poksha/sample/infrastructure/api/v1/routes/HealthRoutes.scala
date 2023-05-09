package com.poksha.sample.infrastructure.api.v1.routes

import cats.effect.IO
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object HealthRoutes {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "health" =>
      Ok(s"OK")
  }
}
