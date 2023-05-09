package com.poksha.sample.domain.auth

import java.util.UUID

case class AuthUserId(value: UUID) extends AnyVal {
  override def toString: String = value.toString
}

object AuthUserId {
  def generate(): AuthUserId = AuthUserId(UUID.randomUUID())

  def fromString(value: String): AuthUserId = AuthUserId(UUID.fromString(value))
}
