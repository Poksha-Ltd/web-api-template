package com.poksha.sample.infrastructure.database.postgres

import com.poksha.sample.domain.auth.AuthUser.EmailPasswordAuthUser
import com.poksha.sample.domain.auth.{AuthUserId, AuthUserPassword}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec

import scala.util.Random

class AuthUserRepositoryPostgresSpec
    extends AnyFlatSpec
    with RepositoryPostgresTestBase
    with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    super.beforeAll()
    setUp()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    cleanUp()
  }

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
