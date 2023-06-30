package com.poksha.sample.application.auth

import com.poksha.sample.application.auth.AuthApplicationError.{
  UnknownApplicationError,
  UserAlreadyExists,
  UserNotFound,
  WrongPassword
}
import com.poksha.sample.domain.auth.{
  AuthUser,
  AuthUserId,
  AuthUserPassword,
  AuthUserRepository
}

trait AuthService {
  def create(c: CreateAuthUserCommand): Either[AuthApplicationError, AuthUserId]
  def authenticate(
      c: UserAuthenticationCommand
  ): Either[AuthApplicationError, AuthUserId]
}

class AuthServiceImpl(implicit authUserRepository: AuthUserRepository)
    extends AuthService {
  override def create(
      c: CreateAuthUserCommand
  ): Either[AuthApplicationError, AuthUserId] = {
    c match {
      case CreateAuthUserCommand.CreatePasswordUser(email, password) =>
        authUserRepository.findByEmail(email) match {
          case Some(_) => Left(UserAlreadyExists)
          case None =>
            val user = AuthUser.EmailPasswordAuthUser(
              AuthUserId.generate(),
              email,
              AuthUserPassword(password).hash()
            )
            authUserRepository
              .save(user)
              .fold(
                failed => {
                  println(s"Fail to save by $failed") // TODO Loggerに変更する
                  Left(UnknownApplicationError)
                },
                created => Right(created.getId)
              )
        }
    }
  }
  override def authenticate(
      c: UserAuthenticationCommand
  ): Either[AuthApplicationError, AuthUserId] = {
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
                  Right(user.getId)
                } else {
                  println(s"Password is invalid") // TODO Loggerに変更する
                  Left(WrongPassword)
                }
            }
          case None => Left(UserNotFound)
        }
    }
  }
}
