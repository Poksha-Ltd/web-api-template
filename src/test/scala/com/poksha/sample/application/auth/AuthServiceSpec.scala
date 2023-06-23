package com.poksha.sample.application.auth

import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserRepository}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class AuthServiceSpec
    extends AnyFlatSpec
    with should.Matchers
    with AuthServiceSpecBase {
  "AuthServiceSpec" should "not create user if the email already exists" in {
    implicit val authUserRepository
        : AuthUserRepository = mockAuthUserRepository(
      mockFindByEmail = {
        case email if email == registeredEmail =>
          Some(
            AuthUser.EmailPasswordAuthUser(
              AuthUserId.generate(),
              "dummy",
              "dummy"
            )
          )
        case email if email == unregisteredEmail => None
        case email =>
          sys.error(
            s"mockAuthUserRepository.findByEmail cannot handle the email [$email]. please set valid email as argument."
          )
      }
    )
    val sut = new AuthService

    val actual = sut.create(
      CreateAuthUserCommand.CreatePasswordUser(registeredEmail, registeredPass)
    )

    actual.isLeft shouldBe true
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
      mockFindFunc: AuthUserId => Option[AuthUser] = _ =>
        throw new NotImplementedError("mockFindFunc is not implemented"),
      mockFindByEmail: String => Option[AuthUser] = _ =>
        throw new NotImplementedError("mockFindByEmail is not implemented"),
      mockSave: AuthUser => Either[String, AuthUser] = _ =>
        throw new NotImplementedError("save is not implemented")
  ): AuthUserRepository = new AuthUserRepository {
    override def find(id: AuthUserId): Option[AuthUser] = mockFindFunc(id)
    override def findByEmail(email: String): Option[AuthUser] = mockFindByEmail(
      email
    )
    override def save(user: AuthUser): Either[String, AuthUser] = mockSave(user)
  }
}
