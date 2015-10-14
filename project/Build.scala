import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._
import play.PlayImport.PlayKeys._
import play.twirl.sbt.Import.TwirlKeys

object ApplicationBuild extends Build {

  import Repos._

  val appName         = "playJSON"
  val appVersion      = "1.0"

  val buildScalaVersion = Seq("2.10.4", "2.11.1")

  //Dependencies
  val casbah = "org.mongodb" %% "casbah" % "2.8.2"
  //val salat = "com.novus" %% "salat" % "1.9.8"
  val config = "com.typesafe" % "config" % "1.2.1"
  val configs = "com.github.kxbmap" %% "configs" % "0.2.2"
  val guice = "com.google.inject" % "guice" % "3.0"
  val sseguice = "com.tzavellas" % "sse-guice" % "0.7.1"

//    val typesafeConfig = "com.typesafe" % "config" % "1.2.1"

  val appDependencies = Seq(jdbc, anorm, casbah, config, configs, guice, sseguice)

  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    version := appVersion,
    scalaVersion := buildScalaVersion.head,
    crossScalaVersions := buildScalaVersion,
    libraryDependencies ++= appDependencies,
    resolvers ++= Seq(typeSafeRepo, typeSafeSnapsRepo, oss, ossSnaps))
}

object Repos {
  val typeSafeRepo = "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
  val typeSafeSnapsRepo = "Typesafe Snaps Repo" at "http://repo.typesafe.com/typesafe/snapshots/"
  val oss = "OSS Sonatype" at "http://oss.sonatype.org/content/repositories/releases/"
  val ossSnaps = "OSS Sonatype Snaps" at "http://oss.sonatype.org/content/repositories/snapshots/"
}
