package com.poksha.sample.infrastructure.database.inMemory

import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserRepository}
import org.slf4j.LoggerFactory

import scala.collection.mutable

object AuthUserRepositoryInMemory extends AuthUserRepository {
  private val logger = LoggerFactory.getLogger(this.getClass.getName)

  private val storage = mutable.Map.empty[AuthUserId, AuthUser]

  override def find(id: AuthUserId): Option[AuthUser] = storage.get(id).orElse {
    logger.debug(s"The user [$id] is not registered")
    None
  }

  override def findByEmail(email: String): Option[AuthUser] =
    storage.values
      .find { case AuthUser.EmailPasswordAuthUser(_, e, _) =>
        e == email
      } orElse {
      logger.debug(s"The email is not registered")
      None
    }

  override def save(user: AuthUser): Either[String, AuthUser] = {
    storage += (user match {
      case AuthUser.EmailPasswordAuthUser(id, _, _) =>
        logger.info(s"Success to save user [$id]")
        (id, user)
    })
    Right(user)
  }
}
