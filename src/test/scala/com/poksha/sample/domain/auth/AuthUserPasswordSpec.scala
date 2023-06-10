package com.poksha.sample.domain.auth

import org.scalatest.flatspec.AnyFlatSpec

class AuthUserPasswordSpec extends AnyFlatSpec {
  "AuthUserPassword" should "has hashed password as value" in {
    val plainPassword = "password"
    val actual = AuthUserPassword(AuthUserPassword(plainPassword).hash())
    assert(actual.value != plainPassword)
  }
}
