package com.poksha.sample.infrastructure.api.v1.routes.auth

import cats.effect._
import com.poksha.sample.infrastructure.api.v1.models.ResponseView.{FailureView, SuccessView}
import com.poksha.sample.infrastructure.api.v1.models.{AuthUserView, ViewError, ViewErrorResponseStatus}
import io.circe.generic.auto._
import org.http4s.Response
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.io._

import scala.util.chaining._

trait AuthResponseCreator {
  def ok(authUserView: AuthUserView): IO[Response[IO]] = {
    authUserView
      .pipe(SuccessView(_))
      .pipe(Ok(_))
  }

  def ng(viewError: ViewError): IO[Response[IO]] = {
    viewError
      .tap(e => println(s"Response error caused by $e")) // TODO Logger を使う
      .pipe(toFailureStatusAndView)
      .pipe { case (status, view) => toFailureResponse(status, view) }
  }

  private def toFailureStatusAndView(err: ViewError): (ViewErrorResponseStatus, FailureView) =
    (err.status, FailureView(err))

  private def toFailureResponse(status: ViewErrorResponseStatus, view: FailureView): IO[Response[IO]] =
    status match {
      case ViewErrorResponseStatus.BadRequest => BadRequest(view)
      case ViewErrorResponseStatus.Forbidden  => Forbidden(view)
    }
}
