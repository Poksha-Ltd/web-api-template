package com.poksha.sample.domain.auth

trait AuthUserRepositoryInterface {
  def find(id: AuthUserId): Option[AuthUser]
  def findByEmail(email: String): Option[AuthUser]
  def save(user: AuthUser): Either[String, AuthUser]
}
