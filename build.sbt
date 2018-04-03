name := """TweetAnalytics"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.196"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.8.0" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "3.0.0" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "1.10.19" % Test
libraryDependencies += "nl.jqno.equalsverifier" % "equalsverifier" % "2.4.5" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.11" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.11" % Test

libraryDependencies += jcache

libraryDependencies ++= Seq(
  ws,
  ehcache
)

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

// Javadoc
sources in (Compile, doc) ~= (_ filter (_.getName endsWith ".java"))
