package com.poksha.sample.application.auth

sealed abstract class UserAuthenticationCommand
object UserAuthenticationCommand {
  case class AuthenticateEmailPasswordUser(email: String, password: String) extends UserAuthenticationCommand
}
