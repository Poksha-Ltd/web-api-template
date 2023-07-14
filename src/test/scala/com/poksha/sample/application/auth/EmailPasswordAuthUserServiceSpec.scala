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
  "updatePassword パスワードの更新" - {
    "登録済みユーザーのパスワード更新に成功する" - {
      "対象のユーザーIDが登録済みの場合" - {
        "パスワードが更新されたユーザーのIDを返す" in {
          val user = dummyAuthedUser()
          val updatePassword = "updated"
          val expected = user.copy(hashedPassword = AuthUserPassword(updatePassword).hash())
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findF = _ => Some(user), saveF = _ => Right(expected))
          val sut = new EmailPasswordAuthUserService

          val actual = sut.updatePassword(UpdateAuthPasswordCommand(user.id.value, updatePassword))

          actual.value shouldBe expected.id
        }
      }
    }

    "登録済みユーザーのパスワード更新に失敗する" - {
      "データベースへの保存処理が失敗した場合" - {
        "不明のエラーを返す" in {
          val user = dummyAuthedUser()
          val updatePassword = "updated"
          implicit val authUserRepository: AuthUserRepository =
            mockAuthUserRepository(findF = _ => Some(user), saveF = _ => Left("saveF error"))
          val sut = new EmailPasswordAuthUserService

          val actual = sut.updatePassword(UpdateAuthPasswordCommand(user.id.value, updatePassword))

          actual shouldBe Left(UnknownApplicationError)
        }
      }

      "対象のユーザーIDが登録されていない場合" - {
        "ユーザーが登録されていないエラーを返す" in {
          implicit val authUserRepository: AuthUserRepository = mockAuthUserRepository(findF = _ => None)
          val sut = new EmailPasswordAuthUserService

          val actual = sut.updatePassword(UpdateAuthPasswordCommand(dummyId, dummyPass))

          actual shouldBe Left(UserNotFound)
        }
      }
    }
  }
}
