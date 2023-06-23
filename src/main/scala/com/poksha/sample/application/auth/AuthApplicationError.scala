package com.poksha.sample.application.auth

sealed trait AuthApplicationError
object AuthApplicationError {
  case object UserAlreadyExists extends AuthApplicationError
  case object InvalidPassword extends AuthApplicationError
  case object UserNotFound extends AuthApplicationError
  case object WrongPassword extends AuthApplicationError
  case object UnknownApplicationError extends AuthApplicationError
}
