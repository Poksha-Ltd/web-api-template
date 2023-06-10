package com.poksha.sample.infrastructure.database.postgres

import cats.effect.IO
import com.poksha.sample.domain.auth.{AuthUser, AuthUserId, AuthUserRepository}
import doobie.implicits.toSqlInterpolator
import doobie.util.transactor.Transactor
import doobie.implicits._
import com.poksha.sample.domain.auth.AuthUser.EmailPasswordAuthUser

case class PostgresConfig private (
    driver: String,
    url: String,
    user: String,
    pass: String
)
object PostgresConfig {
  def apply(
      driver: String = "org.postgresql.Driver",
      url: String = "jdbc:postgresql:authentication",
      user: String = "postgres",
      pass: String = "password"
  ): PostgresConfig = {
    new PostgresConfig(
      driver,
      url,
      user,
      pass
    )
  }
}

case class AuthUserRepositoryPostgres(config: PostgresConfig)
    extends AuthUserRepository {
  import cats.effect.unsafe.implicits.global

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
      .query[(String, String, String)]
      .map { case (id, email, pass) =>
        EmailPasswordAuthUser(AuthUserId.fromString(id), email, pass)
      }
      .option
      .transact(transActor)
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
      .query[(String, String, String)]
      .map { case (id, email, pass) =>
        EmailPasswordAuthUser(AuthUserId.fromString(id), email, pass)
      }
      .option
      .transact(transActor)
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
        )
      """.update.run.attemptSql
      .transact(transActor)
      .map {
        case Right(_) => Right(user)
        case Left(e)  => throw new RuntimeException(e) // TODO 適切なExceptionに変換する
      }
      .unsafeRunSync()
  }
}
