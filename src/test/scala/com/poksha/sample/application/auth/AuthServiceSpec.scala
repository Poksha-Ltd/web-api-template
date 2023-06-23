package com.poksha.sample.application.auth

import com.poksha.sample.application.auth.AuthApplicationError._
import com.poksha.sample.application.auth.CreateAuthUserCommand._
import com.poksha.sample.application.auth.UserAuthenticationCommand.AuthenticateEmailPasswordUser
import com.poksha.sample.domain.auth.AuthUser.EmailPasswordAuthUser
import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserRepository}
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
        val sut = new AuthService

        val actual =
          sut.create(CreatePasswordUser(registeredEmail, registeredPass))

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
          val sut = new AuthService

          val actual = sut.create(CreatePasswordUser("dummyEmail", "dummy"))

          actual shouldBe Left(UnknownApplicationError)
        }
      }

      "and save to storage is succeeded " - {
        "should return created user" in {
          val email = "dummyEmail"
          val password = "dummyPass"
          val authedUser = dummyAuthedUser(email, password)
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(
              findByEmailF = _ => None,
              saveF = _ => Right(authedUser)
            )
          val sut = new AuthService

          val actual =
            sut.create(CreatePasswordUser(email, password))

          actual.value shouldBe authedUser
        }
      }
    }
  }

  "authenticate" - {
    "when the user email is already registered" - {
      "and password is correct" - {
        "should return authed user" in {
          val email = "dummyEmail"
          val password = "dummyPass"
          val authedUser = dummyAuthedUser(email, password)
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findByEmailF = _ => Some(authedUser))
          val sut = new AuthService

          val actual =
            sut.authenticate(AuthenticateEmailPasswordUser(email, password))

          actual.value shouldBe authedUser
        }
      }

      "and password is wrong" - {
        "should return password is invalid" in {
          val email = "dummyEmail"
          val password = "dummyPass"
          val authedUser = dummyAuthedUser(email, password)
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findByEmailF = _ => Some(authedUser))
          val sut = new AuthService

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
        val sut = new AuthService

        val actual =
          sut.authenticate(
            AuthenticateEmailPasswordUser("dummyEmail", "dummyPass")
          )

        actual shouldBe Left(UserNotFound)
      }
    }
  }
}

/** AuthServiceSpec に必要な要素を定義するためのベース trait
  */
sealed trait AuthServiceSpecBase {
  val registeredEmail = "userEmail"
  val registeredPass = "userPass"

  val unregisteredEmail = "unregisteredEmail"

  // FIXME モックを作るライブラリを使うことで分かりやすくなるなら、ライブラリの導入を検討する
  def mockAuthUserRepository(
      findF: AuthUserId => Option[AuthUser] = _ =>
        throw new NotImplementedError("mockFindFunc is not implemented"),
      findByEmailF: String => Option[AuthUser] = _ =>
        throw new NotImplementedError("mockFindByEmail is not implemented"),
      saveF: AuthUser => Either[String, AuthUser] = _ =>
        throw new NotImplementedError("save is not implemented")
  ): AuthUserRepository = new AuthUserRepository {
    override def find(id: AuthUserId): Option[AuthUser] = findF(id)
    override def findByEmail(email: String): Option[AuthUser] = findByEmailF(
      email
    )
    override def save(user: AuthUser): Either[String, AuthUser] = saveF(user)
  }

  def dummyAuthedUser(
      email: String,
      password: String
  ): EmailPasswordAuthUser = {
    import com.poksha.sample.domain.auth.AuthUserPassword
    AuthUser.EmailPasswordAuthUser(
      AuthUserId.generate(),
      email,
      AuthUserPassword(password).hash()
    )
  }
}
