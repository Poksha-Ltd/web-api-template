package com.poksha.sample.application.auth

import com.poksha.sample.application.auth.AuthApplicationError._
import com.poksha.sample.application.auth.EmailPasswordAuthUserServiceCommand.UpdateAuthPasswordCommand
import com.poksha.sample.domain.auth.AuthUser.EmailPasswordAuthUser
import com.poksha.sample.domain.auth._
import org.slf4j.LoggerFactory

import java.util.UUID
import scala.util.chaining.scalaUtilChainingOps

object EmailPasswordAuthUserServiceCommand {
  case class UpdateAuthPasswordCommand(id: UUID, password: String)
}

class EmailPasswordAuthUserService(implicit
    authUserRepository: AuthUserRepository
) {
  private val logger = LoggerFactory.getLogger(this.getClass.getName)

  def updatePassword(
      c: UpdateAuthPasswordCommand
  ): Either[AuthApplicationError, AuthUserId] = {
    authUserRepository.find(AuthUserId(c.id)) match {
      case Some(AuthUser.EmailPasswordAuthUser(id, email, _)) =>
        EmailPasswordAuthUser(id, email, AuthUserPassword(c.password).hash())
          .pipe(authUserRepository.save)
          .fold(
            err =>
              Left(UnknownApplicationError)
                .tap(_ => logger.error(s"Fail to save by $err")),
            updated => Right(updated.getId)
          )

      case None => Left(UserNotFound)
    }
  }
}
