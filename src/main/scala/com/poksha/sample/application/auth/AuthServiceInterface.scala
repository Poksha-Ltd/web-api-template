package com.poksha.sample.application.auth

import com.poksha.sample.domain.auth.AuthUser

trait AuthServiceInterface {
  def create(c: CreateAuthUserCommand): Either[AuthApplicationError, AuthUser]
  def authenticate(
      c: UserAuthenticationCommand
  ): Either[AuthApplicationError, AuthUser]
}
