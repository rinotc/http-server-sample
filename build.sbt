ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"

val ScalaTest = "org.scalatest" %% "scalatest" % "3.2.12"

lazy val `http-server-java-like` = (project in file("http-server-java-like"))
  .settings(
    name := "http-server-sample",
    libraryDependencies ++= Seq(
      ScalaTest % Test
    )
  )

val AkkaVersion = "2.6.19"
lazy val `http-server-akka` = (project in file("http-server-akka"))
  .settings(
    name := "http-server-akka",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed"         % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
      ScalaTest            % Test
    )
  )
