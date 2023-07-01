package com.poksha.sample.application.auth

import com.poksha.sample.application.auth.AuthApplicationError._
import com.poksha.sample.application.auth.AuthServiceCommand.CreateAuthUserCommand.CreatePasswordUser
import com.poksha.sample.application.auth.AuthServiceCommand.UserAuthenticationCommand.AuthenticateEmailPasswordUser
import com.poksha.sample.application.auth.AuthServiceCommand.{CreateAuthUserCommand, UserAuthenticationCommand}
import com.poksha.sample.domain.auth.AuthUser.EmailPasswordAuthUser
import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserPassword, AuthUserRepository}

import scala.util.chaining._

trait AuthService {
  def create(c: CreateAuthUserCommand): Either[AuthApplicationError, AuthUserId]
  def authenticate(
      c: UserAuthenticationCommand
  ): Either[AuthApplicationError, AuthUserId]
}

class AuthServiceImpl(implicit authUserRepository: AuthUserRepository) extends AuthService {
  override def create(
      c: CreateAuthUserCommand
  ): Either[AuthApplicationError, AuthUserId] = {
    c match {
      case CreatePasswordUser(email, password) =>
        authUserRepository.findByEmail(email) match {
          case Some(_) => Left(UserAlreadyExists)
          case None =>
            EmailPasswordAuthUser(AuthUserId.generate(), email, AuthUserPassword(password).hash())
              .pipe(authUserRepository.save(_))
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
      case AuthenticateEmailPasswordUser(email, password) =>
        authUserRepository.findByEmail(email) match {
          case Some(AuthUser.EmailPasswordAuthUser(userId, _, hashedPassword)) =>
            if (AuthUserPassword(password).verify(hashedPassword)) {
              Right(userId)
            } else {
              println(s"Password is invalid") // TODO Loggerに変更する
              Left(WrongPassword)
            }
          case None => Left(UserNotFound)
        }
    }
  }
}

object AuthServiceCommand {

  sealed trait CreateAuthUserCommand
  object CreateAuthUserCommand {
    case class CreatePasswordUser(email: String, password: String) extends CreateAuthUserCommand
  }

  sealed abstract class UserAuthenticationCommand
  object UserAuthenticationCommand {
    case class AuthenticateEmailPasswordUser(email: String, password: String) extends UserAuthenticationCommand
  }

}
