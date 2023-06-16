package com.poksha.sample.infrastructure.api.v1.models

/** API Response Error
  */
sealed class ViewError(val code: Int, val msg: String)
object ViewError {
  case object AlreadyRegistered extends ViewError(101, "User already exists")

  case object AuthenticationFailed
      extends ViewError(201, "Authentication failed")
  case object UserNotFound extends ViewError(202, "User not found")

  case object OtherError extends ViewError(999, "Unknown Error")

  // TODO アプリケーションで発生したエラーをビューのエラーに変換する
  //  アプリケーションエラーの型を定義したら String をその型に置き換える
  type ApplicationError = String
  def fromApplicationError(msg: ApplicationError): ViewError = {
    msg match {
      case "User already exists" => AlreadyRegistered
      case "Invalid password"    => AuthenticationFailed
      case "User not found"      => UserNotFound
      case _                     => OtherError
    }
  }
}
