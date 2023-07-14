package com.poksha.sample.infrastructure.api.v1.routes.auth

import cats.effect._
import com.poksha.sample.application.auth.EmailPasswordAuthUserService
import com.poksha.sample.domain.auth.{AuthUser, AuthUserRepository}
import com.poksha.sample.infrastructure.api.v1.middlewares.AuthJWTMiddleware
import com.poksha.sample.infrastructure.api.v1.models.AuthedUserInput.UpdateUserPasswordInput
import com.poksha.sample.infrastructure.api.v1.models.{AuthUserView, ViewError}
import io.circe.generic.decoding.DerivedDecoder.deriveDecoder
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.dsl.io._
import org.http4s.{AuthedRoutes, HttpRoutes}

class EmailPasswordAuthRoutes(authJWT: AuthJWTMiddleware)(implicit
    authUserRepository: AuthUserRepository
) extends AuthResponseCreator {
  private val emailPasswordAuthUserService = new EmailPasswordAuthUserService()

  private val protectedRoutes: AuthedRoutes[AuthUser, IO] = AuthedRoutes.of {
    case req @ PATCH -> Root / "auth" / "users" / userId / "password" as user =>
      if (user.idAsString != userId) {
        ng(ViewError.IdentificationFailed)
      } else {
        req.req
          .as[UpdateUserPasswordInput]
          .map { input =>
            for {
              com <- input.toCommand
              id <- emailPasswordAuthUserService.updatePassword(com).left.map(ViewError.fromApplicationError)
            } yield id
          }
          .flatMap {
            case Left(viewError) => ng(viewError)
            case Right(userId)   => ok(AuthUserView(userId, authJWT.generateToken(userId)))
          }
      }
  }

  val routes: HttpRoutes[IO] = authJWT.middleware(protectedRoutes)
}
