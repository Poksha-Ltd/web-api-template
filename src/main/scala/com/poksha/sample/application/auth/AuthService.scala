package com.poksha.sample.application.auth

import com.poksha.sample.domain.auth.{
  AuthUser,
  AuthUserId,
  AuthUserPassword,
  AuthUserRepository
}

class AuthService(implicit authUserRepository: AuthUserRepository)
    extends AuthServiceInterface {
  override def create(c: CreateAuthUserCommand): Either[String, AuthUser] = {
    c match {
      case CreateAuthUserCommand.CreatePasswordUser(email, password) =>
        authUserRepository.findByEmail(email) match {
          case Some(_) => Left("User already exists")
          case None =>
            val user = AuthUser.EmailPasswordAuthUser(
              AuthUserId.generate(),
              email,
              AuthUserPassword(password).hash()
            )
            authUserRepository
              .save(user)
              .fold(
                failed => throw new RuntimeException(failed),
                created => Right(created)
              )
        }
    }
  }
  override def authenticate(
      c: UserAuthenticationCommand
  ): Either[String, AuthUser] = {
    c match {
      case UserAuthenticationCommand.AuthenticateEmailPasswordUser(
            email,
            password
          ) =>
        authUserRepository.findByEmail(email) match {
          case Some(user) =>
            user match {
              case AuthUser.EmailPasswordAuthUser(_, _, hashedPassword) =>
                if (AuthUserPassword(password).verify(hashedPassword)) {
                  Right(user)
                } else {
                  Left("Invalid password")
                }
            }
          case None => Left("User not found")
        }
    }
  }
}
