package com.poksha.sample.application.auth

import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserPassword, AuthUserRepository}

class EmailPasswordAuthUserService(implicit authUserRepository: AuthUserRepository) {
  def updatePassword(c: UpdateAuthPasswordCommand): Either[String, AuthUser] = {
    val id = AuthUserId.fromString(c.id)
    authUserRepository.find(id) match {
      case Some(user) =>
        user match {
          case AuthUser.EmailPasswordAuthUser(id, email, _) =>
            val updatedUser = AuthUser.EmailPasswordAuthUser(id, email, AuthUserPassword(c.password).hash())
            authUserRepository.save(updatedUser)
        }
      case None => Left("User not found")
    }
  }
}
