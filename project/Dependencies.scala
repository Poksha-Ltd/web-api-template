import sbt._

object Dependencies {

  // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
  object Slf4j {
    lazy val lib = "org.slf4j" % "slf4j-api" % "2.0.7"
  }

  object Logback {
    lazy val lib = "ch.qos.logback" % "logback-classic" % "1.4.7"
  }

  // https://mvnrepository.com/artifact/at.favre.lib/bcrypt
  object BCrypt {
    lazy val lib = "at.favre.lib" % "bcrypt" % "0.10.2"
  }

  object JwtScala {
    lazy val lib = "com.github.jwt-scala" %% "jwt-circe" % "9.2.0"
  }

  object Doobie {
    lazy val version = "1.0.0-RC1"
    lazy val libs = Seq(
      "org.tpolecat" %% "doobie-core" % version,
      "org.tpolecat" %% "doobie-postgres" % version,
      "org.tpolecat" %% "doobie-specs2" % version
    )
  }

  object Http4s {
    lazy val version = "0.23.18"
    lazy val libs = Seq(
      "org.http4s" %% "http4s-ember-client" % version,
      "org.http4s" %% "http4s-ember-server" % version,
      "org.http4s" %% "http4s-dsl" % version,
      "org.http4s" %% "http4s-circe" % version
    )
  }

  object Circe {
    lazy val version = "0.14.5"
    lazy val libs = Seq(
      // Optional for auto-derivation of JSON codecs
      "io.circe" %% "circe-generic" % Circe.version,
      // Optional for string interpolation to JSON model
      "io.circe" %% "circe-literal" % Circe.version
    )
  }

  object TypesafeConfig {
    lazy val lib = "com.typesafe" % "config" % "1.4.2"
  }

  object ScalaTest {
    lazy val lib = "org.scalatest" %% "scalatest" % "3.2.16" % "test"
  }

  object TestContainers {
    object PostgreSQL {
      lazy val lib = "org.testcontainers" % "postgresql" % "1.18.3" % Test
    }
  }
}
