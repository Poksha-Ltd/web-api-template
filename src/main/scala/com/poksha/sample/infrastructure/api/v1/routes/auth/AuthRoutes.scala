package com.poksha.sample.infrastructure.api.v1.routes.auth

import cats.effect._
import cats.implicits._
import com.poksha.sample.application.auth.AuthService
import com.poksha.sample.application.auth.AuthServiceCommand.CreateAuthUserCommand.CreatePasswordUser
import com.poksha.sample.application.auth.AuthServiceCommand.UserAuthenticationCommand.AuthenticateEmailPasswordUser
import com.poksha.sample.domain.auth.AuthUserRepository
import com.poksha.sample.infrastructure.api.v1.middlewares.AuthJWTMiddleware
import com.poksha.sample.infrastructure.api.v1.models.{AuthUserView, ViewError}
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.Method.POST
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.dsl.io._

class AuthRoutes(
    authService: AuthService,
    authJWT: AuthJWTMiddleware
)(implicit authUserRepository: AuthUserRepository)
    extends AuthResponseCreator {
  private val publicRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "auth" / "signIn" / "emailPassword" =>
      req
        .as[AuthenticateEmailPasswordUser]
        .map(authService.authenticate(_))
        .flatMap { res =>
          res.fold(
            err => ng(ViewError.fromApplicationError(err)),
            userId => ok(AuthUserView(userId, authJWT.generateToken(userId)))
          )
        }

    case req @ POST -> Root / "auth" / "signUp" / "emailPassword" =>
      req
        .as[CreatePasswordUser]
        .map(authService.create(_))
        .flatMap { res =>
          res.fold(
            err => ng(ViewError.fromApplicationError(err)),
            userId => ok(AuthUserView(userId, authJWT.generateToken(userId)))
          )
        }
  }

  val routes: HttpRoutes[IO] =
    publicRoutes <+> new EmailPasswordAuthRoutes(authJWT).routes
}
