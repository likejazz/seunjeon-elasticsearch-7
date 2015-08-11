organization := "org.bitbucket.eunjeon"

name := "seunjeon"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4",
  "com.google.guava" % "guava" % "18.0",
  "junit" % "junit" % "4.12" % "test"
)

publishMavenStyle := true

publishArtifact in Test := false

isSnapshot := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://eunjeon.blogspot.kr/</url>
  <licenses>
    <license>
      <name>The Apache Software License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://bitbucket.org/eunjeon/seunjeon.git</url>
    <connection>scm:git:https://bitbucket.org/eunjeon/seunjeon.git</connection>
  </scm>
  <developers>
    <developer>
      <id>budditao</id>
      <name>yungho yu</name>
      <url>https://www.facebook.com/yungho.yu</url>
    </developer>
    <developer>
      <id>bibreen</id>
      <name>Yong-woon Lee</name>
      <url>https://twitter.com/bibreen</url>
    </developer>
  </developers>
  <issueManagement>
    <url>https://groups.google.com/forum/#!forum/eunjeon</url>
    <system>Google Groups</system>
  </issueManagement>
)
