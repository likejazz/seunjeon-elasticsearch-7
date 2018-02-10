// http://www.scala-sbt.org/release/docs/Using-Sonatype.html
// linux command pgp2랑 헛깔리지말고 sbt의 pgp-cmd 만 사용하면 됨

lazy val commonSettings = Seq(
  organization := "org.bitbucket.eunjeon",
  scalaVersion := "2.12.0",   // default
  publishMavenStyle := true,
  publishArtifact in Test := false,
  useGpg := true,
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
    version := "1.5.0",
    isSnapshot := true,
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8"),
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-jdk14" % "1.7.12" % Runtime,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
      "org.scalatest" %% "scalatest" % "3.0.0" % Test,
      "com.novocode" % "junit-interface" % "0.11" % Test
    )
  )

val elasticsearchPluginName = "elasticsearch-analysis-seunjeon"
val esVersion = "6.1.1"
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
      "org.elasticsearch" % "elasticsearch" % esVersion % Provided,
      "org.apache.logging.log4j" % "log4j-api" % "2.9.1" % Test,
      "org.apache.logging.log4j" % "log4j-core" % "2.9.1" % Test,
//      "org.apache.lucene" % "lucene-test-framework" % "7.0.1" % Test,
//      "org.elasticsearch.test" % "framework" % esVersion % Test,
      "junit" % "junit" % "4.12" % Test,
      "com.novocode" % "junit-interface" % "0.11" % Test,
      "org.scalatest" %% "scalatest" % "3.0.0" % Test
    ),

    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v"),

    test in assembly := {},

    esZip := {
      // craete properties file
      val propertiesFile = file("elasticsearch/target/plugin-descriptor.properties")
      IO.writeLines(propertiesFile, Seq(
        "description=The Korean(seunjeon) analysis plugin.",
        s"version=${version.value}",
        "name=analysis-seunjeon",
        "classname=org.bitbucket.eunjeon.seunjeon.elasticsearch.plugin.analysis.AnalysisSeunjeonPlugin",
        s"java.version=$esJavaVersion",
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

