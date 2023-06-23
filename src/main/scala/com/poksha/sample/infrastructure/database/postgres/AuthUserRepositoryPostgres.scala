package com.poksha.sample.infrastructure.database.postgres

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserRepository}
import doobie.implicits.toSqlInterpolator
import doobie.util.transactor.Transactor
import doobie.implicits._
import com.poksha.sample.domain.auth.AuthUser.EmailPasswordAuthUser
import org.slf4j.LoggerFactory

case class AuthUserDataModel(
    id: String,
    email: String,
    hashedPassword: String
) {
  def toDomain: EmailPasswordAuthUser =
    EmailPasswordAuthUser(id, email, hashedPassword)
}

case class AuthUserRepositoryPostgres(config: PostgresConfig)
    extends AuthUserRepository {

  private val logger = LoggerFactory.getLogger(this.getClass.getName)

  private val transActor = Transactor.fromDriverManager[IO](
    driver = config.driver,
    url = config.url,
    user = config.user,
    pass = config.pass
  )

  override def find(id: AuthUserId): Option[AuthUser] = {
    sql"""
         SELECT 
          id, 
          email, 
          hashed_password 
        FROM auth_user
        WHERE 
          id = ${id.value.toString}
       """
      .query[AuthUserDataModel]
      .map(_.toDomain)
      .option
      .transact(transActor)
      .handleErrorWith(err => IO.pure(handleException(err, None)))
      .unsafeRunSync()
  }

  override def findByEmail(email: String): Option[AuthUser] = {
    sql"""
         SELECT 
          id, 
          email, 
          hashed_password 
        FROM auth_user
        WHERE 
          email = $email
       """
      .query[AuthUserDataModel]
      .map(_.toDomain)
      .option
      .transact(transActor)
      .handleErrorWith(err => IO.pure(handleException(err, None)))
      .unsafeRunSync()
  }

  override def save(user: AuthUser): Either[String, AuthUser] = {
    val emailPasswordAuthUser = user.asInstanceOf[EmailPasswordAuthUser]
    sql"""
        INSERT INTO auth_user (
          id,
          email,
          hashed_password
        ) VALUES (
          ${emailPasswordAuthUser.id.value.toString},
          ${emailPasswordAuthUser.email},
          ${emailPasswordAuthUser.hashedPassword}
        ) ON CONFLICT (
          id
        ) DO UPDATE SET hashed_password=${emailPasswordAuthUser.hashedPassword}
      """.update.run.attemptSql
      .transact(transActor)
      .map {
        case Left(e) =>
          // TODO 適切なExceptionに変換する
          Left(
            handleException(e, s"Update error by $e")
          )
        case Right(_) => Right(user)
      }
      .unsafeRunSync()
  }

  private def handleException[ResultType](
      throwable: Throwable,
      recoveryValue: ResultType
  ): ResultType = {
    logger.error(s"Repository error by $throwable")
    recoveryValue
  }
}
