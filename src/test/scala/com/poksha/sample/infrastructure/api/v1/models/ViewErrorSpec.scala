package com.poksha.sample.infrastructure.api.v1.models

import org.scalatest.flatspec.AnyFlatSpec
import com.poksha.sample.application.auth.{AuthApplicationError => AppError}

class ViewErrorSpec extends AnyFlatSpec {

  "fromApplicationError" should "return AlreadyRegistered if User already exists" in {
    val error = AppError.UserAlreadyExists
    val actual = ViewError.fromApplicationError(error)
    assert(actual == ViewError.AlreadyRegistered)
  }

  "fromApplicationError" should "return AuthenticationFailed if password is wrong" in {
    val error = AppError.WrongPassword
    val actual = ViewError.fromApplicationError(error)
    assert(actual == ViewError.AuthenticationFailed)
  }

  "fromApplicationError" should "return UserNotFound if User not found" in {
    val error = AppError.UserNotFound
    val actual = ViewError.fromApplicationError(error)
    assert(actual == ViewError.UserNotFound)
  }

  "fromApplicationError" should "return OtherError if unknown application error occurred" in {
    val error = AppError.UnknownApplicationError
    val actual = ViewError.fromApplicationError(error)
    assert(actual == ViewError.OtherError)
  }
}
