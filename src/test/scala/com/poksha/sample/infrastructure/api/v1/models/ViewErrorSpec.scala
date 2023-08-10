package com.poksha.sample.infrastructure.api.v1.models

import com.poksha.sample.application.auth.{AuthApplicationError => AppError}
import org.scalatest.freespec.AnyFreeSpec

class ViewErrorSpec extends AnyFreeSpec {

  "fromApplicationError" - {
    "return AlreadyRegistered if User already exists" in {
      val error = AppError.UserAlreadyExists
      val actual = ViewError.fromApplicationError(error)
      assert(actual == ViewError.AlreadyRegistered)
    }
    "return AuthenticationFailed if password is wrong" in {
      val error = AppError.WrongPassword
      val actual = ViewError.fromApplicationError(error)
      assert(actual == ViewError.AuthenticationFailed)
    }

    "return UserNotFound if User not found" in {
      val error = AppError.UserNotFound
      val actual = ViewError.fromApplicationError(error)
      assert(actual == ViewError.UserNotFound)
    }

    "return OtherError if unknown application error occurred" in {
      val error = AppError.UnknownApplicationError
      val actual = ViewError.fromApplicationError(error)
      assert(actual == ViewError.OtherAppError)
    }
  }
}
