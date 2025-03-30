ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.5"

lazy val root = (project in file("."))
  .settings(
    organization := "no.tina",
    name := "trondheim-digital-api",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % "0.23.30",
      "org.http4s" %% "http4s-ember-client" % "0.23.30",
      "org.http4s" %% "http4s-circe" % "0.23.30",
      "org.http4s" %% "http4s-dsl" % "0.23.30",
      "io.circe" %% "circe-generic" % "0.14.12",
      "org.typelevel" %% "cats-effect" % "3.6.0",

      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.6.0" % Test
    )
  )
