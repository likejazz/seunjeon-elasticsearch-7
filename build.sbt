
lazy val commonSettings = Seq(
  organization := "org.bitbucket.eunjeon",
  scalaVersion := "2.12.0",   // default
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith("SNAPSHOT"))
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  isSnapshot := {if (version.value.trim.endsWith("SNAPSHOT")) true else false},
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
    version := "1.3.0",
    crossScalaVersions := Seq("2.11.7", "2.12.0"),
    javacOptions ++= Seq("-source", "1.7", "-target", "1.7"),
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-jdk14" % "1.7.12" % "runtime",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
      "org.scalatest" %% "scalatest" % "3.0.0" % "test",
      "com.novocode" % "junit-interface" % "0.11" % "test"
    )
  )

val elasticsearchPluginName = "elasticsearch-analysis-seunjeon"
val esVersion = "5.1.1"
val esJavaVersion = "1.8"

lazy val elasticsearch = (project in file("elasticsearch")).dependsOn(seunjeon).
  settings(commonSettings: _*).
  settings(
    name := elasticsearchPluginName,

    scalaVersion := "2.12.0",

    version := s"${esVersion}.1",

    javacOptions ++= Seq("-source", esJavaVersion, "-target", esJavaVersion),

    compileOrder := CompileOrder.ScalaThenJava,

    libraryDependencies ++= Seq(
      "org.elasticsearch" % "elasticsearch" % esVersion % "provided",
      "org.apache.logging.log4j" % "log4j-api" % "2.6.2" % "provided",
      "com.novocode" % "junit-interface" % "0.11" % "test"
    ),

    test in assembly := {},

    esZip := {
      // craete properties file
      val propertiesFile = file("elasticsearch/target/plugin-descriptor.properties")
      IO.writeLines(propertiesFile, Seq(
        "description=The Korean(seunjeon) analysis plugin.",
        s"version=${version.value}",
        "name=analysis-seunjeon",
        "classname=org.bitbucket.eunjeon.seunjeon.elasticsearch.plugin.analysis.AnalysisSeunjeonPlugin",
        s"java.version=${esJavaVersion}",
        s"elasticsearch.version=$esVersion"))

      val jarFile = assembly.value
      // create zip file
      val zipFile = file(jarFile.getPath.substring(0, jarFile.getPath.length - jarFile.ext.length - 1) + ".zip")
      IO.zip(
        List(
          (propertiesFile, s"elasticsearch/${propertiesFile.getName}"),
          (jarFile, s"elasticsearch/${jarFile.getName}")),
        zipFile)
      zipFile
    },

    // remove scala version of artifact file.
    crossPaths := false,

    addArtifact(Artifact(elasticsearchPluginName, "zip", "zip"), esZip)
  )


lazy val esZip = taskKey[File]("elasticsearch task")

