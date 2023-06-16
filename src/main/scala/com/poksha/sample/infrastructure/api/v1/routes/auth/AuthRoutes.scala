package com.poksha.sample.infrastructure.api.v1.routes.auth

import cats.effect._
import cats.implicits._
import com.poksha.sample.application.auth.AuthServiceInterface
import com.poksha.sample.application.auth.CreateAuthUserCommand.CreatePasswordUser
import com.poksha.sample.application.auth.UserAuthenticationCommand.AuthenticateEmailPasswordUser
import com.poksha.sample.domain.auth.AuthUserRepository
import com.poksha.sample.infrastructure.api.v1.middlewares.AuthJWTMiddleware
import com.poksha.sample.infrastructure.api.v1.models.Token
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
          authService.authenticate(c) match {
            case Right(user) =>
              ok(user, Token(authJWTMiddleware.generateToken(user)))
            case Left(msg) => badRequest(msg)
          }
        )

    case req @ POST -> Root / "auth" / "signUp" / "emailPassword" =>
      req
        .as[CreatePasswordUser]
        .flatMap { c =>
          authService.create(c) match {
            case Right(createdUser) =>
              ok(
                createdUser,
                Token(authJWTMiddleware.generateToken(createdUser))
              )
            case Left(msg) => badRequest(msg)
          }
        }
  }

  val routes: HttpRoutes[IO] =
    publicRoutes <+> new EmailPasswordAuthRoutes(authJWTMiddleware).routes
}
