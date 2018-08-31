import Commons._
import Dependencies._
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

scalafmtOnCompile in ThisBuild := true

//addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.7" cross CrossVersion.binary)

lazy val root = Project(id = "mass-data-root", base = file("."))
  .aggregate(
    example,
    massDocs,
    massFunctest,
    massRdi,
    massRdiCli,
    massRdiCore,
    massConnector,
    massGovernance,
    massScheduler,
    massApiService,
    massConsole,
    massAuth,
    massBroker,
    massCoreExt,
    massCore,
    massCommon
  )
  .settings(Publishing.noPublish: _*)
  .settings(Environment.settings: _*)

lazy val massDocs = _project("mass-docs")
  .enablePlugins(ParadoxPlugin)
  .dependsOn(
    massFunctest,
    massRdi,
    massRdiCli,
    massRdiCore,
    massConnector,
    massGovernance,
    massApiService,
    massConsole,
    massAuth,
    massBroker,
    massCoreExt % "compile->compile;test->test",
    massCore % "compile->compile;test->test",
    massCommon
  )
  .settings(
    name in (Compile, paradox) := "massData",
    paradoxTheme := Some(builtinParadoxTheme("generic")),
    paradoxProperties ++= Map(
      "github.base_url" -> s"https://github.com/yangbajing/mass-data/tree/${version.value}",
      "scala.version" -> scalaVersion.value,
      "scala.binary_version" -> scalaBinaryVersion.value,
      "scaladoc.akka.base_url" -> s"http://doc.akka.io/api/$versionAkka",
      "akka.version" -> versionAkka
    )
  )
  .settings(Publishing.noPublish: _*)

lazy val example = _project("example")
  .settings(Publishing.publishing: _*)
  .dependsOn(massCoreExt % "compile->compile;test->test", massCore % "compile->compile;test->test")
  .settings(
    libraryDependencies ++= _akkaClusters ++ _akkaHttps //++ _kamons
  )

lazy val massFunctest = _project("mass-functest")
  .dependsOn(massConsole,
             massBroker,
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
  .dependsOn(massCoreExt % "compile->compile;test->test", massCore % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)
  .settings(Packaging.settings: _*)
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("mass.apiservice.boot.ApiServiceMain"),
    libraryDependencies ++= Seq(
      ) ++ _akkaHttps
  )

// 数据治理
lazy val massGovernance = _project("mass-governance")
  .dependsOn(massConnector, massCoreExt % "compile->compile;test->test", massCore % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)
  .settings(Packaging.settings: _*)
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("mass.governance.boot.GovernanceMain"),
    libraryDependencies ++= Seq(
      )
  )

// 监查、控制、管理
lazy val massConsole = _project("mass-console")
  .dependsOn(massCoreExt % "compile->compile;test->test", massCore % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)
  .settings(Packaging.settings: _*)
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("mass.console.boot.ConsoleMain"),
    libraryDependencies ++= Seq(
      )
  )

// Reactive Data Integration Console
lazy val massRdiConsole = _project("mass-rdi-console")
  .dependsOn(massRdiCore, massCoreExt % "compile->compile;test->test", massCore % "compile->compile;test->test")
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("mass.rdi.console.boot.RdiConsoleMain"),
    libraryDependencies ++= Seq(
      _quartz
    ) ++ _pois
  )

// Reactive Data Integration 反应式数据处理流工具
lazy val massRdi = _project("mass-rdi")
  .dependsOn(massCoreExt,
             massRdiCore,
             massCoreExt % "compile->compile;test->test",
             massCore % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)
  .settings(Packaging.settings: _*)
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("mass.rdi.boot.RdiMain"),
    libraryDependencies ++= Seq(
      _quartz
    ) ++ _pois
  )

// Reactive Data Integration Cli
lazy val massRdiCli = _project("mass-rdi-cli")
  .dependsOn(massRdiCore, massCoreExt % "compile->compile;test->test", massCore % "compile->compile;test->test")
  .settings(Publishing.noPublish: _*)
  .settings(
    //    assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false),
    assemblyJarName in assembly := "rdi.jar",
    mainClass in Compile := Some("mass.rdi.cli.boot.RdiCliMain")
  )

lazy val massRdiCore = _project("mass-rdi-core")
  .dependsOn(massConnector, massCore % "compile->compile;test->test")
  .settings(Publishing.noPublish: _*)
  .settings(
    libraryDependencies ++= Seq(
      _commonsVfs,
      _quartz % Provided
    )
  )

// mass调度任务程序.
lazy val massScheduler = _project("mass-scheduler")
  .dependsOn(massCoreExt % "compile->compile;test->test", massCore % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)
  .settings(Packaging.settings: _*)
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("mass.scheduler.boot.SchedulerMain"),
    libraryDependencies ++= Seq(
      )
  )

// 统一用户，OAuth 2服务
lazy val massAuth = _project("mass-auth")
  .dependsOn(massCoreExt % "compile->compile;test->test", massCore % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)
  .settings(Packaging.settings: _*)
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("mass.auth.boot.AuthMain"),
    libraryDependencies ++= Seq(
      )
  )

// 集群代理节点
lazy val massBroker = _project("mass-broker")
  .dependsOn(massCoreExt % "compile->compile;test->test", massCore % "compile->compile;test->test")
  .enablePlugins(JavaAppPackaging)
  .settings(Packaging.settings: _*)
  .settings(Publishing.noPublish: _*)
  .settings(
    mainClass in Compile := Some("mass.broker.boot.BrokerMain"),
    libraryDependencies ++= Seq(
      _alpakkaFile,
      _alpakkaFtp
    )
  )

// 数据组件（采集、存储）
lazy val massConnector = _project("mass-connector")
  .dependsOn(massCore % "compile->compile;test->test")
  .settings(
    mainClass in Compile := Some("mass.connector.boot.ConnectorMain"),
    libraryDependencies ++= Seq(
      _akkaStreamKafka,
      _mssql,
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
      _fastparse,
      _quartz,
      _jsch,
      _sigarLoader
    ) ++ _akkaClusters ++ _slicks ++ _macwires ++ _akkaManagements
  )

lazy val massCore = _project("mass-core")
  .dependsOn(massCommon % "compile->compile;test->test")
  .settings(Publishing.publishing: _*)
  .settings(
    libraryDependencies ++= Seq(
      _protobuf,
      _shapeless,
      _scopt,
      _scalaXml,
      _hikariCP,
      _h2,
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
      _postgresql % Test,
      _quartz % Provided
    ) ++ _catses ++ _circes ++ _akkaHttps,
    PB.targets in Compile := Seq(
      scalapb.gen(flatPackage = true) -> (sourceManaged in Compile).value
    )
  )

lazy val massCommon = _project("mass-common")
  .settings(Publishing.publishing: _*)
  .settings(
    libraryDependencies ++= Seq(
      //      _swaggerAnnotation % Provided,
      _config,
      "org.scala-lang" % "scala-library" % scalaVersion.value,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      _scalaJava8Compat,
      _scalatest % Test
    ) ++ _jsons ++ _akkas ++ _logs
  )

def _project(name: String, _base: String = null) =
  Project(id = name, base = file(if (_base eq null) name else _base))
    .settings(basicSettings: _*)
//    .settings(inConfig(Integration)(org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings): _*)
