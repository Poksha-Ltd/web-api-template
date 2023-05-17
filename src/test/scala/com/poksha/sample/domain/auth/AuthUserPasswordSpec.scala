package com.poksha.sample.domain.auth

import org.scalatest.wordspec.AnyWordSpec

class AuthUserPasswordSpec extends AnyWordSpec {
  "verify" when {
    "string hashing the same string as base plain password" should {
      "return true" in {
        val basePlainPassword = "password"
        val password = AuthUserPassword(basePlainPassword)
        assert(password.verify(password.hash()))
      }
    }
  }
}