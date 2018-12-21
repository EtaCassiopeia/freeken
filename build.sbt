name := "freeken"

version := "0.1"

scalaVersion := "2.12.8"

lazy val catsVersion = "1.5.0"

libraryDependencies ++= Seq("org.typelevel" %% "cats-core" % catsVersion,
                            "org.typelevel" %% "cats-free" % catsVersion)
