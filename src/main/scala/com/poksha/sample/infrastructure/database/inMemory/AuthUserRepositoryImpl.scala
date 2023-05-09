package com.poksha.sample.infrastructure.database.inMemory

import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserRepositoryInterface}

import scala.collection.mutable

object AuthUserRepositoryImpl extends AuthUserRepositoryInterface {
  private val storage = mutable.Map.empty[AuthUserId, AuthUser]

  override def find(id: AuthUserId): Option[AuthUser] = storage.get(id)

  override def findByEmail(email: String): Option[AuthUser] = storage.values.find {
    case AuthUser.EmailPasswordAuthUser(_, e, _) => e == email
  }

  override def save(user: AuthUser): Either[String, AuthUser]  = {
    storage += (user match {
      case AuthUser.EmailPasswordAuthUser(id, _, _) => (id, user)
    })
    Right(user)
  }
}
