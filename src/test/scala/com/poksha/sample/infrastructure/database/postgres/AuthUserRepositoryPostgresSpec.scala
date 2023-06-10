package com.poksha.sample.infrastructure.database.postgres

import com.poksha.sample.domain.auth.AuthUser.EmailPasswordAuthUser
import com.poksha.sample.domain.auth.{AuthUserId, AuthUserPassword}
import com.poksha.sample.infrastructure.database.postgres.AuthUserRepositoryPostgresSpec.testConfig
import org.scalatest.flatspec.AnyFlatSpec

import scala.util.Random

class AuthUserRepositoryPostgresSpec extends AnyFlatSpec {
  "AuthUserRepositoryPostgres" should "save new user with email" in {
    val sut = AuthUserRepositoryPostgres(testConfig)
    val email = s"${Random.alphanumeric.take(10).mkString}@example.com"
    sut.save(
      EmailPasswordAuthUser(
        AuthUserId.generate(),
        email,
        AuthUserPassword("pass").hash()
      )
    )
    assert(sut.findByEmail(email).isDefined)
  }
}
object AuthUserRepositoryPostgresSpec {
  private val testConfig = PostgresConfig(
    url = "jdbc:postgresql:test_authentication",
    user = "postgres",
    pass = "password"
  )
}
