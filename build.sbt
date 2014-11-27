name := "personal-phb"

version := "0.1.0"

scalaVersion := "2.11.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "org.apache.pdfbox" % "pdfbox" % "1.8.7"
)
