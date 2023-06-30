package com.poksha.sample.application.auth

import com.poksha.sample.application.auth.AuthApplicationError._
import com.poksha.sample.application.auth.CreateAuthUserCommand._
import com.poksha.sample.application.auth.UserAuthenticationCommand.AuthenticateEmailPasswordUser
import com.poksha.sample.domain.auth.AuthUserRepository
import org.scalatest.EitherValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should

class AuthServiceSpec
    extends AnyFreeSpec
    with should.Matchers
    with AuthServiceSpecBase
    with EitherValues {
  "create" - {
    "when the email already exists" - {
      "should return user already exists error" in {
        implicit val authUserRepository: AuthUserRepository =
          mockAuthUserRepository(
            findByEmailF = email => Some(dummyAuthedUser(email, "dummy"))
          )
        val sut = new AuthServiceImpl

        val actual =
          sut.create(CreatePasswordUser(dummyEmail, dummyPass))

        actual shouldBe Left(UserAlreadyExists)
      }
    }
    "when the email is not exists" - {
      "and save to storage is failed" - {
        "should return unknown error" in {
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(
              findByEmailF = _ => None,
              saveF = _ => Left("saveF error")
            )
          val sut = new AuthServiceImpl

          val actual = sut.create(CreatePasswordUser(dummyEmail, dummyPass))

          actual shouldBe Left(UnknownApplicationError)
        }
      }

      "and save to storage is succeeded " - {
        "should return created user" in {
          val email = dummyEmail
          val password = dummyPass
          val authedUser = dummyAuthedUser(email, password)
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(
              findByEmailF = _ => None,
              saveF = _ => Right(authedUser)
            )
          val sut = new AuthServiceImpl

          val actual =
            sut.create(CreatePasswordUser(email, password))

          actual.value shouldBe authedUser.getId
        }
      }
    }
  }

  "authenticate" - {
    "when the user email is already registered" - {
      "and password is correct" - {
        "should return authed user" in {
          val email = dummyEmail
          val password = dummyPass
          val authedUser = dummyAuthedUser(email, password)
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findByEmailF = _ => Some(authedUser))
          val sut = new AuthServiceImpl

          val actual =
            sut.authenticate(AuthenticateEmailPasswordUser(email, password))

          actual.value shouldBe authedUser.getId
        }
      }

      "and password is wrong" - {
        "should return password is invalid" in {
          val email = dummyEmail
          val password = dummyPass
          val authedUser = dummyAuthedUser(email, password)
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findByEmailF = _ => Some(authedUser))
          val sut = new AuthServiceImpl

          val actual =
            sut.authenticate(AuthenticateEmailPasswordUser(email, "wrongPass"))

          actual shouldBe Left(WrongPassword)
        }
      }
    }

    "when the user email is not registered" - {
      "should return user not found error" in {
        implicit val authUserRepository: AuthUserRepository =
          mockAuthUserRepository(findByEmailF = _ => None)
        val sut = new AuthServiceImpl

        val actual =
          sut.authenticate(AuthenticateEmailPasswordUser(dummyEmail, dummyPass))

        actual shouldBe Left(UserNotFound)
      }
    }
  }
}
