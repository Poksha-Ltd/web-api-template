package com.poksha.sample.infrastructure.api.v1.models

import com.poksha.sample.application.auth.EmailPasswordAuthUserServiceCommand.UpdateAuthPasswordCommand
import com.poksha.sample.infrastructure.api.v1.models.ViewError.IllegalInputError

import java.util.UUID

sealed trait AuthedUserInput {
  def id: String
}
object AuthedUserInput {
  case class UpdateUserPasswordInput(id: String, password: String) extends AuthedUserInput {
    def toCommand: Either[ViewError, UpdateAuthPasswordCommand] = {
      try {
        if (id.isEmpty || password.isEmpty) { throw new IllegalArgumentException }
        Right(UpdateAuthPasswordCommand(UUID.fromString(id), password))
      } catch {
        case _: Throwable => Left(IllegalInputError)
      }
    }
  }
}
