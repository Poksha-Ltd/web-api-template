package com.poksha.sample.infrastructure.database.postgres

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
