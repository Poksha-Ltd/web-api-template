package com.poksha.sample.application.auth

import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserRepository}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class AuthServiceSpec extends AnyFlatSpec with should.Matchers with AuthServiceSpecBase {
  "AuthServiceSpec" should "not create user if the email already exists" in {
    val sut = new AuthService
    val actual = sut.create(
      CreateAuthUserCommand.CreatePasswordUser(registeredEmail, registeredPass)
    )
    actual.isLeft shouldBe true
  }
}

/**
 * AuthServiceSpec に必要な要素を定義するためのベース trait
 */
sealed trait AuthServiceSpecBase {
  val registeredEmail = "userEmail"
  val registeredPass = "userPass"

  val unregisteredEmail = "unregisteredEmail"

  // FIXME モックを作るライブラリを使うことで分かりやすくなるなら、ライブラリの導入を検討する
  implicit val mockAuthUserRepository: AuthUserRepository = new AuthUserRepository {
    override def find(id: AuthUserId): Option[AuthUser] = throw new NotImplementedError("find is not implemented")
    override def findByEmail(email: String): Option[AuthUser] = email match {
      case _ if email == registeredEmail   => Some(AuthUser.EmailPasswordAuthUser(AuthUserId.generate(), "dummy", "dummy"))
      case _ if email == unregisteredEmail => None
      case _                               => sys.error(s"mockAuthUserRepository.findByEmail cannot handle the email [$email]. please set valid email as argument.")
    }
    override def save(user: AuthUser): Either[String, AuthUser] = throw new NotImplementedError("save is not implemented")
  }
}
