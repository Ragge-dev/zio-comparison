ThisBuild / scalaVersion := "2.13.12"
ThisBuild / resolvers += "Kognic maven repo" at "artifactregistry://europe-west1-maven.pkg.dev/annotell-com/maven-default"

lazy val dependencies = Seq(
  "dev.zio" %% "zio" % "2.0.13",
  "dev.zio" %% "zio-test" % "2.0.13",
  "dev.zio" %% "zio-test-sbt" % "2.0.13" % Test,
  "dev.zio" %% "zio-mock" % "1.0.0-RC11" % Test,
  "com.kognic.platform" % "core-tagging" % "5.2.0",
  "com.kognic.platform" % "json" % "5.2.0",
  "com.kognic.platform" % "utils" % "5.2.0",

)

ThisBuild / scalacOptions ++= Seq(
  "-Xsource:3",
  "-language:implicitConversions",
  "-Wunused:imports",
)

lazy val root = project
  .in(file("."))
  .aggregate(helloWorld, helloService)

lazy val helloWorld = project
  .in(file("hello-world"))
  .settings(
    name := "hello-world",
    version := "0.1.0-SNAPSHOT",
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= dependencies
  )

lazy val helloService = project
  .in(file("hello-service"))
  .settings(
    name := "hello-service",
    version := "0.1.0-SNAPSHOT",
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= dependencies
  )