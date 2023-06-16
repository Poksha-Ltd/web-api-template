package com.poksha.sample.infrastructure.api.v1.routes.auth

import cats.effect._
import com.poksha.sample.domain.auth.AuthUser
import com.poksha.sample.infrastructure.api.v1.models.ResponseView.{
  FailureView,
  SuccessView
}
import com.poksha.sample.infrastructure.api.v1.models.{
  AuthUserView,
  Token,
  ViewError
}
import io.circe.generic.auto._
import org.http4s.Response
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._

import scala.util.chaining._

trait AuthResponseCreator {
  def ok(user: AuthUser, token: Token): IO[Response[IO]] = {
    AuthUserView(user, token)
      .pipe(authUserView => SuccessView(authUserView))
      .pipe(success => Ok(success))
  }

  def badRequest(msg: String): IO[Response[IO]] = {
    ViewError
      .fromApplicationError(msg)
      .pipe(error => FailureView(error))
      .pipe(BadRequest(_))
  }

  def forbidden(msg: String): IO[Response[IO]] = {
    ViewError
      .fromApplicationError(msg)
      .pipe(error => FailureView(error))
      .pipe(Forbidden(_))
  }
}
