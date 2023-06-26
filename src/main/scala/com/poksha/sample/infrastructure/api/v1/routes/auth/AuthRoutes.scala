package com.poksha.sample.infrastructure.api.v1.routes.auth

import cats.effect._
import cats.implicits._
import com.poksha.sample.application.auth.AuthServiceInterface
import com.poksha.sample.application.auth.CreateAuthUserCommand.CreatePasswordUser
import com.poksha.sample.application.auth.UserAuthenticationCommand.AuthenticateEmailPasswordUser
import com.poksha.sample.domain.auth.AuthUserRepository
import com.poksha.sample.infrastructure.api.v1.middlewares.AuthJWTMiddleware
import com.poksha.sample.infrastructure.api.v1.models.{
  AuthUserView,
  Token,
  ViewError
}
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.Method.POST
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.dsl.io._

class AuthRoutes(
    authService: AuthServiceInterface,
    authJWTMiddleware: AuthJWTMiddleware
)(implicit authUserRepository: AuthUserRepository)
    extends AuthResponseCreator {
  private val publicRoutes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req @ POST -> Root / "auth" / "signIn" / "emailPassword" =>
      req
        .as[AuthenticateEmailPasswordUser]
        .flatMap(c =>
          authService
            .authenticate(c)
            .fold(
              err => ng(ViewError.fromApplicationError(err)),
              user =>
                ok(
                  AuthUserView(
                    user,
                    Token(authJWTMiddleware.generateToken(user))
                  )
                )
            )
        )

    case req @ POST -> Root / "auth" / "signUp" / "emailPassword" =>
      req
        .as[CreatePasswordUser]
        .flatMap { c =>
          authService
            .create(c)
            .fold(
              err => ng(ViewError.fromApplicationError(err)),
              user =>
                ok(
                  AuthUserView(
                    user,
                    Token(authJWTMiddleware.generateToken(user))
                  )
                )
            )
        }
  }

  val routes: HttpRoutes[IO] =
    publicRoutes <+> new EmailPasswordAuthRoutes(authJWTMiddleware).routes
}
