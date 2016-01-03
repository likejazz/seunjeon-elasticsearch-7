lazy val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "org.bitbucket.eunjeon",
  version := "1.0.0-SNAPSHOT",

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

// TODO: http://stackoverflow.com/questions/27466869/download-a-zip-from-url-and-extract-it-in-resource-using-sbt

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
    )
  )

lazy val elasticsearch = (project in file("elasticsearch")).dependsOn(seunjeon).
  settings(commonSettings: _*).
  settings(
    name := "elasticsearch-analysis-seunjeon",

    libraryDependencies ++= Seq(
      "org.elasticsearch" % "elasticsearch" % "2.1.0" % "provided",
      "junit" % "junit" % "4.12" % "test"
    ),

    assembly <<= assembly map { (f: File) =>
      val zipPath = f.getPath.substring(0, f.getPath.length - f.ext.length - 1) + ".zip"
      val zipFile = file(zipPath)

      val propertiesFile = file("elasticsearch/src/main/resources/plugin-descriptor.properties")
      IO.zip(List((f, f.toPath.getFileName.toString), (propertiesFile, propertiesFile.toPath.getFileName.toString)), zipFile)
      println("GENERATED PACKAGE LOCATION:  " + zipPath)
      zipFile
    },
    assemblyJarName in assembly := s"${name.value}-${version.value}.jar",
    //assemblyDefaultJarName in assembly := s"${name.value}-${version.value}.zip",

//    publishArtifact in (Compile, packageBin) := false,
//
////    artifact in (Compile, assembly) := {
////      val art = (artifact in (Compile, assembly)).value
////      art.copy(`classifier` = Some("assembly"))
////    },
//
//    addArtifact(artifact in (Compile, assembly), assembly),

    test in assembly := {}
  )

