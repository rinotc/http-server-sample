ThisBuild / version := "0.1.0-SNAPSHOT"

val ScalaTest = "org.scalatest" %% "scalatest" % "3.2.12"

lazy val `http-server-java-like` = (project in file("http-server-java-like"))
  .settings(
    scalaVersion := "3.1.3",
    name         := "http-server-sample",
    libraryDependencies ++= Seq(
      ScalaTest % Test
    )
  )
