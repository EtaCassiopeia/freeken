name := "freeken"

version := "0.1"

scalaVersion := "2.12.8"

lazy val catsVersion = "1.5.0"
lazy val circeVersion = "0.11.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-free" % catsVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.scalaj" %% "scalaj-http" % "2.4.1"
)
