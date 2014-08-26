import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._
import play.PlayImport.PlayKeys._
import play.twirl.sbt.Import.TwirlKeys

object ApplicationBuild extends Build {

  import Repos._
  
  val appName         = "playJSON"
  val appVersion      = "1.0-SNAPSHOT"
    
  val buildScalaVersion = "2.11.1"
    
  //Dependencies
  val casbah = "org.mongodb" %% "casbah" % "2.7.3"  
  val salat = "com.novus" %% "salat" % "1.9.8" 
  val appDependencies = Seq(jdbc, anorm, salat)

  val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
    version := appVersion,
    scalaVersion := buildScalaVersion,
    libraryDependencies ++= appDependencies,
    resolvers ++= Seq(typeSafeRepo, typeSafeSnapsRepo, oss, ossSnaps))
}

object Repos {
  val typeSafeRepo = "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"
  val typeSafeSnapsRepo = "Typesafe Snaps Repo" at "http://repo.typesafe.com/typesafe/snapshots/"
  val oss = "OSS Sonatype" at "http://oss.sonatype.org/content/repositories/releases/"
  val ossSnaps = "OSS Sonatype Snaps" at "http://oss.sonatype.org/content/repositories/snapshots/"
}