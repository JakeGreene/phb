name := "personal-phb"

version := "0.1.0"

scalaVersion := "2.11.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "org.apache.pdfbox" % "pdfbox" % "1.8.7",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.h2database" % "h2" % "1.4.182", // Used during testing
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4", // Used in Heroku
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.2",
  jdbc
)
