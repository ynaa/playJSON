 name 			:= "playJSON"
 version 		:= "1.0"
 scalaVersion 	:= "2.11.7"

//Dependencies
val casbah = "org.mongodb" %% "casbah" % "2.8.2"
  
val config1 = "com.typesafe" % "config" % "1.3.0"
  
val test1 = "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

val typeSafeRepo = "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

val typeSafeSnapsRepo = "Typesafe Snaps Repo" at "http://repo.typesafe.com/typesafe/snapshots/"

val oss = "OSS Sonatype" at "http://oss.sonatype.org/content/repositories/releases/"

val ossSnaps = "OSS Sonatype Snaps" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(casbah, config1, test1)

resolvers ++= Seq(typeSafeRepo, typeSafeSnapsRepo, oss, ossSnaps)
  
lazy val main = (project in file(".")).enablePlugins(PlayScala)

