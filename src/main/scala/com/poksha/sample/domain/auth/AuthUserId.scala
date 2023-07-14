package com.poksha.sample.domain.auth

import java.util.UUID

case class AuthUserId private (value: UUID) extends AnyVal {
  override def toString: String = value.toString
}

object AuthUserId {
  def generate(): AuthUserId = AuthUserId(UUID.randomUUID())
  def apply(value: UUID): AuthUserId = new AuthUserId(value)
}
