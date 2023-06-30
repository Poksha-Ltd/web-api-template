package com.poksha.sample.infrastructure.api.v1.routes

import cats.effect.IO
import cats.implicits._
import com.poksha.sample.application.auth.AuthService
import com.poksha.sample.domain.auth.AuthUserRepository
import com.poksha.sample.infrastructure.api.v1.middlewares.AuthJWTMiddleware
import com.poksha.sample.infrastructure.api.v1.routes.auth.AuthRoutes
import org.http4s.HttpRoutes

object V1Routes {
  def routes(
      authService: AuthService
  )(implicit authUserRepository: AuthUserRepository): HttpRoutes[IO] =
    HealthRoutes.routes <+> new AuthRoutes(
      authService,
      new AuthJWTMiddleware()
    ).routes
}
