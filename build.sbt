ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

lazy val `http-server-java-like` = (project in file("http-server-java-like"))
  .settings(
    name := "http-server-sample",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.12" % Test
    )
  )

lazy val `http-server-akka` = (project in file("http-server-akka"))
  .settings(
    name := "http-server-akka",
    libraryDependencies ++= Seq(
    )
  )
