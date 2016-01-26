lazy val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "org.bitbucket.eunjeon",
  javacOptions ++= Seq("-source", "1.7", "-target", "1.7"),

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
    crossScalaVersions := Seq("2.11.7", "2.10.6"),
    name := "seunjeon",

    version := "1.0.1",

    libraryDependencies ++= Seq(
      "com.github.takawitter" % "trie4j" % "0.9.1",
      "com.github.tototoshi" %% "scala-csv" % "1.2.2",
      "org.slf4j" % "slf4j-jdk14" % "1.7.12" % "runtime",
      "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
      "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
      "junit" % "junit" % "4.12" % "test"
    )
  )

val elasticsearchPluginName = "elasticsearch-analysis-seunjeon"
lazy val elasticsearch = (project in file("elasticsearch")).dependsOn(seunjeon).
  settings(commonSettings: _*).
  settings(
    name := elasticsearchPluginName,

    version := "2.1.1.0",

    libraryDependencies ++= Seq(
      "org.elasticsearch" % "elasticsearch" % "2.1.1" % "provided",
      "junit" % "junit" % "4.12" % "test"
    ),

    test in assembly := {},

    esZip := {
      val propertiesFile = file("elasticsearch/src/main/resources/plugin-descriptor.properties")
      val assemblyFile = assembly.value
      val zipFile = file(assemblyFile.getPath.substring(0, assemblyFile.getPath.length - assemblyFile.ext.length - 1) + ".zip")
      IO.zip(List(
        (propertiesFile, propertiesFile.toPath.getFileName.toString),
        (assemblyFile, assemblyFile.toPath.getFileName.toString)), zipFile)
      zipFile
    },

    // remove scala version of artifact file.
    crossPaths := false,

    addArtifact(Artifact(elasticsearchPluginName, "zip", "zip"), esZip)
  )


lazy val esZip = taskKey[File]("elasticsearch task")

