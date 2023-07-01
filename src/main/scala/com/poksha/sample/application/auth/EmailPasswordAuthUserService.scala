package com.poksha.sample.application.auth

import com.poksha.sample.application.auth.AuthApplicationError._
import com.poksha.sample.application.auth.EmailPasswordAuthUserServiceCommand.UpdateAuthPasswordCommand
import com.poksha.sample.domain.auth.AuthUser.EmailPasswordAuthUser
import com.poksha.sample.domain.auth._

import scala.util.chaining.scalaUtilChainingOps

class EmailPasswordAuthUserService(implicit
    authUserRepository: AuthUserRepository
) {
  def updatePassword(
      c: UpdateAuthPasswordCommand
  ): Either[AuthApplicationError, AuthUserId] = {
    val id = AuthUserId.fromString(c.id)
    authUserRepository.find(id) match {
      case Some(AuthUser.EmailPasswordAuthUser(id, email, _)) =>
        EmailPasswordAuthUser(id, email, AuthUserPassword(c.password).hash())
          .pipe(authUserRepository.save)
          .fold(
            err => {
              println(s"Fail to save by $err") // TODO Loggerに変更する
              Left(UnknownApplicationError)
            },
            updated => Right(updated.getId)
          )

      case None => Left(UserNotFound)
    }
  }
}

object EmailPasswordAuthUserServiceCommand {
  case class UpdateAuthPasswordCommand(id: String, password: String)
}
