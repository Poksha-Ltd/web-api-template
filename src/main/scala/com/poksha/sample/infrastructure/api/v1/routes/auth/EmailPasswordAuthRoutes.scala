package com.poksha.sample.infrastructure.api.v1.routes.auth

import cats.effect._
import com.poksha.sample.application.auth.{
  EmailPasswordAuthUserService,
  UpdateAuthPasswordCommand
}
import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserRepository}
import com.poksha.sample.infrastructure.api.v1.middlewares.AuthJWTMiddleware
import com.poksha.sample.infrastructure.api.v1.models.{
  AuthUserView,
  Token,
  ViewError
}
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.dsl.io._
import org.http4s.{AuthedRoutes, HttpRoutes}

class EmailPasswordAuthRoutes(authJWT: AuthJWTMiddleware)(implicit
    authUserRepository: AuthUserRepository
) extends AuthResponseCreator {
  private val protectedRoutes: AuthedRoutes[AuthUser, IO] = AuthedRoutes.of {
    case req @ PATCH -> Root / "auth" / "users" / userId / "password" as user =>
      val authUserId = AuthUserId.fromString(userId)
      if (user.getId != authUserId) {
        ng(ViewError.IdentificationFailed)
      } else {
        req.req.as[UpdateAuthPasswordCommand].flatMap { com =>
          new EmailPasswordAuthUserService()
            .updatePassword(com)
            .fold(
              err => ng(ViewError.fromApplicationError(err)),
              id => ok(AuthUserView(id, Token(authJWT.generateToken(id))))
            )
        }
      }
  }

  val routes: HttpRoutes[IO] = authJWT.middleware(protectedRoutes)
}
