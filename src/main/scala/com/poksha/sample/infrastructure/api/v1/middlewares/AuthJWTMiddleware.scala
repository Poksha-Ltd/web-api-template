package com.poksha.sample.infrastructure.api.v1.middlewares

import cats.data._
import cats.effect._
import cats.implicits._
import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserRepository}
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.headers.Authorization
import org.http4s.server._
import pdi.jwt._

import scala.concurrent.duration.DurationInt

case class JwtToken(accessToken: String, tokenType: String = "bearer")

class AuthJWTMiddleware(implicit authUserRepository: AuthUserRepository) {
  private val algo = JwtAlgorithm.HS256
  private val privateKey = "privateKey" // TODO: move to config

  def generateToken(userId: AuthUserId): JwtToken = {
    val claim = JwtClaim(
      expiration = Some(System.currentTimeMillis + 30.minutes.toMillis),
      issuedAt = Some(System.currentTimeMillis),
      content = userId.asJson.toString
    )
    JwtToken(JwtCirce.encode(claim, privateKey, algo))
  }

  private val auth: Kleisli[IO, Request[IO], Either[String, AuthUser]] =
    Kleisli { request =>
      val token = request.headers.get[Authorization] match {
        case Some(Authorization(Credentials.Token(AuthScheme.Bearer, token))) =>
          Some(token)
        case _ => None
      }
      val claim = token match {
        case None => "No token found".asLeft[JwtClaim]
        case Some(value) =>
          pdi.jwt.Jwt
            .decode(value, privateKey, Seq(algo))
            .toEither
            .leftMap(_ => "Invalid token")
      }

      claim
        .flatMap { claim =>
          parser
            .parse(claim.content)
            .flatMap(_.as[AuthUserId])
            .leftMap(_ => "Invalid token")
            .map(authUserRepository.find)
            .flatMap(_.toRight("User not found"))
        }
        .pure[IO]

    }
  private val onFailure: AuthedRoutes[String, IO] =
    Kleisli(req => OptionT.liftF(Forbidden(req.context)))

  val middleware: AuthMiddleware[IO, AuthUser] = AuthMiddleware(auth, onFailure)

}
