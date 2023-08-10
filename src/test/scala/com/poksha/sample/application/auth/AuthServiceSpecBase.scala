package com.poksha.sample.application.auth

import com.poksha.sample.domain.auth.AuthUser.EmailPasswordAuthUser
import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserRepository}

import java.util.UUID

/** AuthServiceSpec に必要な要素を定義するためのベース trait
  */
trait AuthServiceSpecBase {
  lazy val dummyId: UUID = UUID.randomUUID()
  val dummyEmail = "email"
  val dummyPass = "pass"

  // FIXME モックを作るライブラリを使うことで分かりやすくなるなら、ライブラリの導入を検討する
  def mockAuthUserRepository(
      findF: AuthUserId => Option[AuthUser] = _ => throw new NotImplementedError("mockFindFunc is not implemented"),
      findByEmailF: String => Option[AuthUser] = _ =>
        throw new NotImplementedError("mockFindByEmail is not implemented"),
      saveF: AuthUser => Either[String, AuthUser] = _ => throw new NotImplementedError("save is not implemented")
  ): AuthUserRepository = new AuthUserRepository {
    override def find(id: AuthUserId): Option[AuthUser] = findF(id)
    override def findByEmail(email: String): Option[AuthUser] = findByEmailF(
      email
    )
    override def save(user: AuthUser): Either[String, AuthUser] = saveF(user)
  }

  def dummyAuthedUser(
      email: String = dummyEmail,
      password: String = dummyPass
  ): EmailPasswordAuthUser = {
    import com.poksha.sample.domain.auth.AuthUserPassword
    AuthUser.EmailPasswordAuthUser(
      AuthUserId.generate(),
      email,
      AuthUserPassword(password).hash()
    )
  }
}
