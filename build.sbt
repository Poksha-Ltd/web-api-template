ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.17"

addSbtPlugin("io.spray" % "sbt-revolver" % "0.10.0")

// https://mvnrepository.com/artifact/at.favre.lib/bcrypt
libraryDependencies += "at.favre.lib" % "bcrypt" % "0.10.2"

// https://mvnrepository.com/artifact/org.slf4j/slf4j-api
libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.7"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.4.7"

val http4sVersion = "0.23.18"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-ember-client" % http4sVersion,
  "org.http4s" %% "http4s-ember-server" % http4sVersion,
  "org.http4s" %% "http4s-dsl" % http4sVersion
)

libraryDependencies += "com.github.jwt-scala" %% "jwt-circe" % "9.2.0"

val scalatest = "org.scalatest" %% "scalatest" % "3.2.16" % "test"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, RevolverPlugin)
  .settings(
    fork / run := true
  )
  .settings(
    name := "sample",
    libraryDependencies ++= Seq(
      scalatest
    )
  )

Revolver.enableDebugging(port = 5005, suspend = false)
reStartArgs := Seq("--watch", "src/main/scala")

scalacOptions ++= Seq("-Ypartial-unification")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-circe" % http4sVersion,
  // Optional for auto-derivation of JSON codecs
  "io.circe" %% "circe-generic" % "0.14.5",
  // Optional for string interpolation to JSON model
  "io.circe" %% "circe-literal" % "0.14.5"
)

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)

lazy val doobieVersion = "1.0.0-RC1"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2" % doobieVersion
)

libraryDependencies += "com.typesafe" % "config" % "1.4.2"
