package com.poksha.sample.application.auth

import com.poksha.sample.application.auth.AuthApplicationError._
import com.poksha.sample.application.auth.AuthServiceCommand.CreateAuthUserCommand.CreatePasswordUser
import com.poksha.sample.application.auth.AuthServiceCommand.UserAuthenticationCommand.AuthenticateEmailPasswordUser
import com.poksha.sample.domain.auth.AuthUserRepository
import org.scalatest.EitherValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should

class AuthServiceSpec extends AnyFreeSpec with should.Matchers with AuthServiceSpecBase with EitherValues {
  "create 新規ユーザー作成" - {
    "ユーザーが作成できる" - {
      "異常系ケースに入らない場合" - {
        "作成したユーザーのIDを返す" in {
          val email = dummyEmail
          val password = dummyPass
          val authedUser = dummyAuthedUser(email, password)
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findByEmailF = _ => None, saveF = _ => Right(authedUser))
          val sut = new AuthServiceImpl

          val actual = sut.create(CreatePasswordUser(email, password))

          actual.value shouldBe authedUser.getId
        }
      }
    }

    "ユーザー作成に失敗する" - {
      "メールアドレスが既に登録されている場合" - {
        "ユーザーが既に登録されているエラーを返す" in {
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findByEmailF = email => Some(dummyAuthedUser(email, "dummy")))
          val sut = new AuthServiceImpl

          val actual = sut.create(CreatePasswordUser(dummyEmail, dummyPass))

          actual shouldBe Left(UserAlreadyExists)
        }
      }

      "データベースへの保存が失敗した場合" - {
        "不明のエラーを返す" in {
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findByEmailF = _ => None, saveF = _ => Left("saveF error"))
          val sut = new AuthServiceImpl

          val actual = sut.create(CreatePasswordUser(dummyEmail, dummyPass))

          actual shouldBe Left(UnknownApplicationError)
        }
      }
    }
  }

  "authenticate 登録済みユーザーの認証" - {
    "ユーザーが認証できる" - {
      "メールアドレスとパスワードが登録されたものと同一の場合" - {
        "認証されたユーザーを返す" in {
          val email = dummyEmail
          val password = dummyPass
          val authedUser = dummyAuthedUser(email, password)
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findByEmailF = _ => Some(authedUser))
          val sut = new AuthServiceImpl

          val actual = sut.authenticate(AuthenticateEmailPasswordUser(email, password))

          actual.value shouldBe authedUser.getId
        }
      }
    }

    "ユーザー認証に失敗する" - {
      "メールアドレスは正しいが、パスワードが登録したものと一致しない場合" - {
        "パスワード誤りのエラーを返す" in {
          val email = dummyEmail
          val password = dummyPass
          val authedUser = dummyAuthedUser(email, password)
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findByEmailF = _ => Some(authedUser))
          val sut = new AuthServiceImpl

          val actual = sut.authenticate(AuthenticateEmailPasswordUser(email, "wrongPass"))

          actual shouldBe Left(WrongPassword)
        }
      }

      "メールアドレスが登録されていない場合" - {
        "ユーザーが存在しないエラーを返す" in {
          implicit val authUserRepository: AuthUserRepository = mockAuthUserRepository(findByEmailF = _ => None)
          val sut = new AuthServiceImpl

          val actual = sut.authenticate(AuthenticateEmailPasswordUser(dummyEmail, dummyPass))

          actual shouldBe Left(UserNotFound)
        }
      }
    }
  }
}
