lazy val akkaHttpVersion = "10.2.6"
lazy val akkaVersion = "2.6.17"

lazy val root = (project in file(".")).settings(
  inThisBuild(
    List(
      organization := "com.lgajowy",
      scalaVersion := "2.13.4"
    )
  ),
  name := "crypto-payments",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.softwaremill.sttp.tapir" %% "tapir-akka-http-server" % "0.19.0-M13",
    "com.softwaremill.sttp.tapir" %% "tapir-json-spray" % "0.19.0-M13",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "0.19.0-M13",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % "0.19.0-M13",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
    "org.scalatest" %% "scalatest" % "3.1.4" % Test
  )
)
