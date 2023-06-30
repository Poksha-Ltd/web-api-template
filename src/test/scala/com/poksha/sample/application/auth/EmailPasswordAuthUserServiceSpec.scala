package com.poksha.sample.application.auth

import com.poksha.sample.application.auth.AuthApplicationError._
import com.poksha.sample.application.auth.EmailPasswordAuthUserServiceCommand.UpdateAuthPasswordCommand
import com.poksha.sample.domain.auth.{AuthUserPassword, AuthUserRepository}
import org.scalatest.EitherValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should

class EmailPasswordAuthUserServiceSpec
    extends AnyFreeSpec
    with should.Matchers
    with AuthServiceSpecBase
    with EitherValues {
  "updatePassword" - {
    "when the user email is already registered" - {
      "and save to storage is failed" - {
        "should return unknown error" in {
          val user = dummyAuthedUser()
          val updatePassword = "updated"
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(
              findF = _ => Some(user),
              saveF = _ => Left("saveF error")
            )
          val sut = new EmailPasswordAuthUserService

          val actual = sut.updatePassword(
            UpdateAuthPasswordCommand(user.id.toString, updatePassword)
          )

          actual shouldBe Left(UnknownApplicationError)
        }
      }

      "and save to storage is succeeded" - {
        "should return updated user" in {
          val user = dummyAuthedUser()
          val updatePassword = "updated"
          val expected =
            user.copy(hashedPassword = AuthUserPassword(updatePassword).hash())
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(
              findF = _ => Some(user),
              saveF = _ => Right(expected)
            )
          val sut = new EmailPasswordAuthUserService

          val actual = sut.updatePassword(
            UpdateAuthPasswordCommand(user.id.toString, updatePassword)
          )

          actual.value shouldBe expected.id
        }
      }
    }

    "when the user email is not registered" - {
      "should return user not found error" in {
        implicit val authUserRepository: AuthUserRepository =
          mockAuthUserRepository(findF = _ => None)
        val sut = new EmailPasswordAuthUserService

        val actual =
          sut.updatePassword(UpdateAuthPasswordCommand(dummyId, dummyPass))

        actual shouldBe Left(UserNotFound)
      }
    }
  }
}
