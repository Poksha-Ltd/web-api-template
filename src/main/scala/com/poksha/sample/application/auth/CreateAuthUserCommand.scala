package com.poksha.sample.application.auth

sealed abstract class CreateAuthUserCommand
object CreateAuthUserCommand {
  case class CreatePasswordUser(email: String, password: String) extends CreateAuthUserCommand
}
