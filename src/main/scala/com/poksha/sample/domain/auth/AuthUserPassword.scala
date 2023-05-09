package com.poksha.sample.domain.auth

import at.favre.lib.crypto.bcrypt.BCrypt


case class AuthUserPassword(value: String) extends AnyVal {
  def hash(): String = {
    BCrypt.withDefaults().hashToString(12, value.toCharArray)
  }
  def verify(hashedPassword: String): Boolean = {
    BCrypt.verifyer().verify(value.toCharArray, hashedPassword).verified
  }
}
