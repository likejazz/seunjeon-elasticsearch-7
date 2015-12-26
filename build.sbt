lazy val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "org.bitbucket.eunjeon",
  version := "0.7.0-SNAPSHOT"
)

lazy val seunjeon = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "seunjeon",
    libraryDependencies ++= Seq(
      "com.github.takawitter" % "trie4j" % "0.9.1",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "com.github.tototoshi" %% "scala-csv" % "1.2.2",
      "org.slf4j" % "slf4j-jdk14" % "1.7.12" % "runtime",
      "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
      "junit" % "junit" % "4.12" % "test"
    ),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (version.value.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    pomIncludeRepository := { _ => false },
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
  )

lazy val elasticsearch = (project in file("elasticsearch")).dependsOn(seunjeon).
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "org.elasticsearch" % "elasticsearch" % "2.1.1",
      "junit" % "junit" % "4.12" % "test"
    ),

    assemblyMergeStrategy in assembly := {
      case PathList("org", "joda", "time", "base", "BaseDateTime.class") => new IncludeFromJar("joda-time-2.8.2.jar")
      case x => (assemblyMergeStrategy in assembly).value(x)
    },

    assembly <<= assembly map { (f: File) =>
      val zipPath = f.getPath.substring(0, f.getPath.length - f.ext.length - 1) + ".zip"
      val zipFile = file(zipPath)
      IO.zip(List((f, f.toPath.getFileName.toString)), zipFile)
      println("GENERATED PACKAGE LOCATION:  " + zipPath)
      zipFile
    }
  )
