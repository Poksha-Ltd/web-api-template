package com.poksha.sample.infrastructure.api.v1.models

import com.poksha.sample.domain.auth.AuthUserId
import com.poksha.sample.infrastructure.api.v1.middlewares.JwtToken

case class AuthUserView(id: String, token: JwtToken)
object AuthUserView {
  def apply(userId: AuthUserId, token: JwtToken): AuthUserView =
    AuthUserView(userId.toString, token)
}
