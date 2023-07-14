package com.poksha.sample.domain.auth

import java.util.UUID

sealed abstract class AuthUser(id: AuthUserId) {
  def getId: AuthUserId = id
  def idAsString: String = id.value.toString
}
object AuthUser {
  case class EmailPasswordAuthUser(id: AuthUserId, email: String, hashedPassword: String) extends AuthUser(id)
  object EmailPasswordAuthUser {
    def of(uuid: UUID, email: String, hashedPassword: String): EmailPasswordAuthUser =
      new EmailPasswordAuthUser(AuthUserId(uuid), email, hashedPassword)
  }
}
