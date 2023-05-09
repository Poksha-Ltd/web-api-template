package com.poksha.sample.domain.auth

sealed abstract class AuthUser(id: AuthUserId) {
  def getId: AuthUserId = id
}
object AuthUser {
  case class EmailPasswordAuthUser(id: AuthUserId, email: String, hashedPassword: String) extends AuthUser(id)
}
