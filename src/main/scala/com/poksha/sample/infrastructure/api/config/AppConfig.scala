package com.poksha.sample.infrastructure.api.config

case class AppConfig private (
    port: Int,
    host: String,
)

object AppConfig {
  def apply(
      port: Int = 8080,
      host: String = "0.0.0.0"
   ): AppConfig = {
    new AppConfig(
      port,
      host,
    )
  }
}
