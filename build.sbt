lazy val `example-akka-remoting` =
  project.in(file("."))
  .aggregate(
    `api`,
    `a`,
    `b`,
    `c`
  )
  .settings(commonSettings: _*)
  .enablePlugins()

lazy val `api` =
  project.in(file("api"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= commonLibraries
  )
  .enablePlugins()

lazy val `a` =
  project.in(file("a"))
  .dependsOn(`api`)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= commonLibraries,
    version in Docker := "a"
  )
  .enablePlugins(JavaServerAppPackaging, OpenShiftPlugin)
  .settings(dockerSettings: _*)

lazy val `b` =
  project.in(file("b"))
  .dependsOn(`api`)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= commonLibraries,
    version in Docker := "b"
  )
  .enablePlugins(JavaServerAppPackaging, OpenShiftPlugin)
  .settings(dockerSettings: _*)

lazy val `c` =
  project.in(file("c"))
  .dependsOn(`api`)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= commonLibraries,
    version in Docker := "c"
  )
  .enablePlugins(JavaServerAppPackaging, OpenShiftPlugin)
  .settings(dockerSettings: _*)


lazy val commonSettings = Seq(
  organization := "com.ctc.example",
  version := "0.1",

  scalaVersion := "2.12.2",
  parallelExecution := false,

  scalacOptions ++= Seq(
    "-encoding", "UTF-8",

    "-feature",
    "-unchecked",
    "-deprecation",

    "-language:postfixOps",
    "-language:implicitConversions",

    "-Ywarn-unused-import",
    "-Xfatal-warnings",
    "-Xlint:_"
  ),
  concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)
)

lazy val commonLibraries = {
  val akkaVersion = "2.5.2"
  val akkaHttpVersion = "10.0.8"
  val scalatestVersion = "3.0.3"

  Seq(
    "com.iheart" %% "ficus" % "1.4.0",
    "io.spray" %% "spray-json" % "1.3.3",

    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,

    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

    "org.scalactic" %% "scalactic" % scalatestVersion % Test,
    "org.scalatest" %% "scalatest" % scalatestVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
  )
}

lazy val dockerSettings = {
  Seq(
    dockerExposedPorts := Seq(9000),
    dockerRepository := Some("wassj"),
    packageName in Docker := "example-akka-remoting"
  )
}
