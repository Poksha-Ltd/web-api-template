import Dependencies._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.17"

addSbtPlugin("io.spray" % "sbt-revolver" % "0.10.0")

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging, RevolverPlugin)
  .settings(
    fork / run := true
  )
  .settings(
    name := "sample",
    libraryDependencies ++= Seq(
      BCrypt.lib,
      Slf4j.lib,
      Logback.lib,
      JwtScala.lib,
      TypesafeConfig.lib,
      ScalaTest.lib,
      TestContainers.PostgreSQL.lib
    ) ++ Circe.libs
      ++ Doobie.libs
      ++ Http4s.libs
  )

Revolver.enableDebugging(port = 5005, suspend = false)
reStartArgs := Seq("--watch", "src/main/scala")

scalacOptions ++= Seq("-Ypartial-unification")

addCompilerPlugin(
  "org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full
)
