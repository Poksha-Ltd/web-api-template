package com.poksha.sample.infrastructure.api.v1.models

import com.poksha.sample.domain.auth.AuthUser

case class AuthUserView(id: String, token: Token)
object AuthUserView {
  def apply(user: AuthUser, token: Token): AuthUserView = AuthUserView(user.getId.toString, token)
}