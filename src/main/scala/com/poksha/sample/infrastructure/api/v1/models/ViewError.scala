package com.poksha.sample.infrastructure.api.v1.models

import com.poksha.sample.application.auth.AuthApplicationError
import com.poksha.sample.application.auth.{AuthApplicationError => AppError}

/** API Response Error
  */
sealed class ViewError(val code: Int, val msg: String)
object ViewError {
  case object AlreadyRegistered extends ViewError(101, "User already exists")

  case object AuthenticationFailed
      extends ViewError(201, "Authentication failed")
  case object UserNotFound extends ViewError(202, "User not found")

  case object OtherError extends ViewError(999, "Unknown Error")

  def fromApplicationError(err: AuthApplicationError): ViewError = {
    err match {
      case AppError.UserAlreadyExists       => AlreadyRegistered
      case AppError.WrongPassword           => AuthenticationFailed
      case AppError.UserNotFound            => UserNotFound
      case AppError.UnknownApplicationError => OtherError
    }
  }
}
