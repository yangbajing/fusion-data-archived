import Commons._
import Dependencies._
import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

lazy val root = Project(id = "mass-data-root", base = file("."))
  .aggregate(
    massApiService,
    massDocs,
    massFunctest,
    massConsole,
    massBroker,
    massConnector,
    massCoreExt,
    example,
    massCore,
    massCommon
  )
  .settings(Publishing.noPublish: _*)
  .settings(Environment.settings: _*)

lazy val massDocs = _project("mass-docs")
  .enablePlugins(ParadoxPlugin)
  .dependsOn(
    massFunctest, massConsole, massBroker,
    massCoreExt % "compile->compile;test->test",
    massCore % "compile->compile;test->test"
  )
  .settings(
    name in(Compile, paradox) := "massData",
    paradoxTheme := Some(builtinParadoxTheme("generic")),
    paradoxProperties ++= Map(
      "github.base_url" -> s"https://github.com/yangbajing/mass-data/tree/${version.value}",
      "scala.version" -> scalaVersion.value,
      "scala.binary_version" -> scalaBinaryVersion.value,
      "scaladoc.akka.base_url" -> s"http://doc.akka.io/api/$versionAkka",
      "akka.version" -> versionAkka
    ))
  .settings(Publishing.noPublish: _*)

lazy val massFunctest = _project("mass-functest")
  .dependsOn(massConsole, massBroker,
    massCoreExt % "compile->compile;test->test",
    massCore % "compile->compile;test->test")
  .enablePlugins(MultiJvmPlugin)
  .configs(MultiJvm)
  .settings(Publishing.noPublish: _*)
  .settings(
    jvmOptions in MultiJvm := Seq("-Xmx1024M"),
    libraryDependencies ++= Seq(
      _akkaMultiNodeTestkit
    ) //++ _kamons
  )

// API Service
lazy val massApiService = _project("mass-api-service")
  .dependsOn(massCore % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)
  .settings(Packaging.settings: _*)
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("massdata.apiservice.boot.ApiServiceMain"),
    libraryDependencies ++= Seq(

    ) ++ _akkaHttps
  )

// 监查、控制、管理
lazy val massConsole = _project("mass-console")
  .dependsOn(
    massCoreExt % "compile->compile;test->test",
    massCore % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)
  .settings(Packaging.settings: _*)
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("massdata.console.boot.ConsoleMain"),
    libraryDependencies ++= Seq(

    )
  )

// 执行引擎
lazy val massBroker = _project("mass-broker")
  .dependsOn(
    massCoreExt % "compile->compile;test->test",
    massCore % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)
  .settings(Packaging.settings: _*)
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("massdata.broker.boot.BrokerMain"),
    libraryDependencies ++= Seq(
      _quartz,
      _alpakkaFile,
      _alpakkaFtp
    )
  )

// 数据组件（采集、存储）
lazy val massConnector = _project("mass-connector")
  .settings(Publishing.noPublish: _*)
  .dependsOn(
    massCore % "compile->compile;test->test")
  .settings(
    //    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    mainClass in Compile := Some("massdata.connector.boot.ConnectorMain"),
    libraryDependencies ++= Seq(
      _akkaStreamKafka,
      _mysql,
      _postgresql
    ) ++ _alpakkas ++ _alpakkaNoSQLs
  )

// 管理程序公共库
lazy val massCoreExt = _project("mass-core-ext")
  .settings(Publishing.publishing: _*)
  .dependsOn(massCore % "compile->compile;test->test")
  .settings(
    libraryDependencies ++= Seq(
      _sigarLoader
    ) ++ _akkaClusters ++ _akkaHttps //++ _kamons
  )

lazy val example = _project("example")
  .settings(Publishing.publishing: _*)
  .dependsOn(massCore % "compile->compile;test->test")
  .settings(
    libraryDependencies ++= _akkaClusters ++ _akkaHttps //++ _kamons
  )


lazy val massCore = _project("mass-core")
  .dependsOn(massCommon % "compile->compile;test->test")
  .settings(Publishing.publishing: _*)
  .settings(
    libraryDependencies ++= Seq(
      _protobuf,
      _shapeless,
      _postgresql,
      _scopt,
      _akkaHttpCore % Provided
    ) ++ _catses ++ _circes ++ _slicks
  )

lazy val massCommon = _project("mass-common")
  .settings(Publishing.publishing: _*)
  .settings(
    libraryDependencies ++= Seq(
      //      _swaggerAnnotation % Provided,
      _hikariCP,
      _scalaLogging,
      _logbackClassic,
      _config,
      _scalaXml,
      "org.scala-lang" % "scala-library" % scalaVersion.value,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      _scalaJava8Compat,
      _mysql % Test,
      _postgresql % Test,
      _scalatest % Test
    ) ++ _jacksons ++ _akkas
  )

def _project(name: String, _base: String = null) =
  Project(id = name, base = file(if (_base eq null) name else _base))
    .settings(basicSettings: _*)
