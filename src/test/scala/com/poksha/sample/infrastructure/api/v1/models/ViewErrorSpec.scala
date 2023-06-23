package com.poksha.sample.infrastructure.api.v1.models

import org.scalatest.flatspec.AnyFlatSpec

class ViewErrorSpec extends AnyFlatSpec {

  "fromApplicationError" should "return AlreadyRegistered if User already exists" in {
    val error = "User already exists"
    val actual = ViewError.fromApplicationError(error)
    assert(actual == ViewError.AlreadyRegistered)
  }

  "fromApplicationError" should "return AuthenticationFailed if Invalid password" in {
    val error = "Invalid password"
    val actual = ViewError.fromApplicationError(error)
    assert(actual == ViewError.AuthenticationFailed)
  }

  "fromApplicationError" should "return UserNotFound if User not found" in {
    val error = "User not found"
    val actual = ViewError.fromApplicationError(error)
    assert(actual == ViewError.UserNotFound)
  }
}
