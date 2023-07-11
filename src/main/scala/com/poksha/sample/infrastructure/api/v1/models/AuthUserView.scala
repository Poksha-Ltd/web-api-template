package com.poksha.sample.infrastructure.api.v1.models

import com.poksha.sample.domain.auth.{AuthUser, AuthUserId}

case class AuthUserView(id: String, token: Token)
object AuthUserView {
  def apply(userId: AuthUserId, token: Token): AuthUserView =
    AuthUserView(userId.toString, token)
}
