package com.poksha.sample.application.auth

import com.poksha.sample.domain.auth.AuthUserId

trait AuthServiceInterface {
  def create(c: CreateAuthUserCommand): Either[AuthApplicationError, AuthUserId]
  def authenticate(
      c: UserAuthenticationCommand
  ): Either[AuthApplicationError, AuthUserId]
}
