package com.poksha.sample.infrastructure.api.v1.models

import com.poksha.sample.application.auth.AuthApplicationError
import com.poksha.sample.application.auth.{AuthApplicationError => AppError}
import com.poksha.sample.infrastructure.api.v1.models.ViewErrorResponseStatus._

/** Error in infrastructure view layer
  */
sealed class ViewError(
    val code: Int,
    val msg: String,
    val status: ViewErrorResponseStatus
)
object ViewError {

  // Error in view layer
  case object IdentificationFailed extends ViewError(101, "You can only change your own password", Forbidden)
  case object IllegalInputError extends ViewError(102, "You should confirm request arguments", BadRequest)

  // Error in app layer
  case object AlreadyRegistered extends ViewError(201, "User already exists", BadRequest)
  case object AuthenticationFailed extends ViewError(202, "Authentication failed", BadRequest)
  case object UserNotFound extends ViewError(203, "User not found", BadRequest)
  case object OtherAppError extends ViewError(299, "Unknown Error", BadRequest)

  def fromApplicationError(err: AuthApplicationError): ViewError = err match {
    case AppError.UserAlreadyExists       => AlreadyRegistered
    case AppError.WrongPassword           => AuthenticationFailed
    case AppError.UserNotFound            => UserNotFound
    case AppError.UnknownApplicationError => OtherAppError
  }
}

sealed trait ViewErrorResponseStatus
object ViewErrorResponseStatus {
  case object BadRequest extends ViewErrorResponseStatus
  case object Forbidden extends ViewErrorResponseStatus
}
