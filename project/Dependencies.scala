import fusion.sbt.gen.BuildInfo
import sbt._

object Dependencies {
  val versionScala = "2.13.1"
  val versionScalaLib = "2.13"

  val _scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.2.0"

  val _fusionProtobufV3 = "com.akka-fusion" %% "fusion-protobuf-v3" % BuildInfo.version
  val _fusionJdbc = "com.akka-fusion" %% "fusion-jdbc" % BuildInfo.version
  val _fusionCluster = "com.akka-fusion" %% "fusion-cluster" % BuildInfo.version
  val _fusionCore = "com.akka-fusion" %% "fusion-core" % BuildInfo.version
  val _fusionCommon = "com.akka-fusion" %% "fusion-common" % BuildInfo.version
  val _fusionTestkit = "com.akka-fusion" %% "fusion-testkit" % BuildInfo.version

  val _akkaDiscovery = "com.typesafe.akka" %% "akka-discovery" % BuildInfo.versionAkka
  val _akkaSerializationJackson = "com.typesafe.akka" %% "akka-serialization-jackson" % BuildInfo.versionAkka
  val _akkaPersistenceTyped = "com.typesafe.akka" %% "akka-persistence-typed" % BuildInfo.versionAkka
  val _akkaPersistenceQuery = "com.typesafe.akka" %% "akka-persistence-query" % BuildInfo.versionAkka
  val _akkaClusterMetrics = "com.typesafe.akka" %% "akka-cluster-metrics" % BuildInfo.versionAkka
  val _akkaMultiNodeTestkit = "com.typesafe.akka" %% "akka-multi-node-testkit" % BuildInfo.versionAkka

  val _akkaHttp = ("com.typesafe.akka" %% "akka-http" % BuildInfo.versionAkkaHttp)
    .exclude("com.typesafe.akka", "akka-stream")
    .cross(CrossVersion.binary)

  val _akkaHttpTestkit = ("com.typesafe.akka" %% "akka-http-testkit" % BuildInfo.versionAkkaHttp)
    .exclude("com.typesafe.akka", "akka-stream-testkit")
    .cross(CrossVersion.binary)
    .exclude("com.typesafe.akka", "akka-testkit")
    .cross(CrossVersion.binary)

  val _akkaHttp2 = ("com.typesafe.akka" %% "akka-http2-support" % BuildInfo.versionAkkaHttp)
    .exclude("com.typesafe.akka", "akka-stream")
    .cross(CrossVersion.binary)

  val _akkaHttps = Seq(_akkaHttp, _akkaHttp2, _akkaHttpTestkit % Test)

  val _alpakkaSimpleCodecs =
    ("com.lightbend.akka" %% "akka-stream-alpakka-simple-codecs" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkaXml =
    ("com.lightbend.akka" %% "akka-stream-alpakka-xml" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkaCsv =
    ("com.lightbend.akka" %% "akka-stream-alpakka-csv" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkaJsonStreaming =
    ("com.lightbend.akka" %% "akka-stream-alpakka-json-streaming" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkaFile =
    ("com.lightbend.akka" %% "akka-stream-alpakka-file" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkaFtp =
    ("com.lightbend.akka" %% "akka-stream-alpakka-ftp" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkaUnixDomainSocket =
    ("com.lightbend.akka" %% "akka-stream-alpakka-unix-domain-socket" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkaMongodb =
    ("com.lightbend.akka" %% "akka-stream-alpakka-mongodb" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkaCassandra =
    ("com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % BuildInfo.versionAlpakka).excludeAll(
      ExclusionRule("com.typesafe.akka"),
      ExclusionRule("com.datastax.cassandra"),
      ExclusionRule("io.netty"),
      ExclusionRule("com.google.guava"))

  val _alpakkaElasticsearch =
    ("com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkaHbase =
    ("com.lightbend.akka" %% "akka-stream-alpakka-hbase" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakksHdfs =
    ("com.lightbend.akka" %% "akka-stream-alpakka-hdfs" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkaText =
    ("com.lightbend.akka" %% "akka-stream-alpakka-text" % BuildInfo.versionAlpakka)
      .excludeAll(ExclusionRule("com.typesafe.akka"))

  val _alpakkas = Seq(
    _alpakkaText,
    _alpakkaSimpleCodecs,
    _alpakkaXml,
    _alpakkaCsv,
    _alpakkaJsonStreaming,
    _alpakkaFile,
    _alpakkaFtp,
    _alpakkaUnixDomainSocket)

  val _alpakkaNoSQLs = Seq(
    _alpakkaMongodb,
    _alpakkaCassandra,
    //                           _alpakkaHbase,
    //                           _alpakksHdfs,
    _alpakkaElasticsearch)

  private val versionAkkaPersistenceCassandra = "0.89"

  val _akkaPersistenceCassandras = Seq(
    "com.typesafe.akka" %% "akka-persistence-cassandra" % versionAkkaPersistenceCassandra,
    "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % versionAkkaPersistenceCassandra % Test)

  val _akkaStreamKafka = ("com.typesafe.akka" %% "akka-stream-kafka" % "2.0.1")
    .exclude("com.typesafe.akka", "akka-slf4j")
    .cross(CrossVersion.binary)

  val _hanlp = "com.hankcs" % "hanlp" % "portable-1.7.1"

  val _scopt = "com.github.scopt" %% "scopt" % "3.7.1"

  val _slicks = Seq(
    "com.typesafe.slick" %% "slick" % BuildInfo.versionSlick,
    "com.github.tminglei" %% "slick-pg" % "0.18.1",
    "com.typesafe.slick" %% "slick-testkit" % BuildInfo.versionSlick % Test)

  private val versionPoi = "4.1.1"
  val _pois = Seq("org.apache.poi" % "poi-scratchpad" % versionPoi, "org.apache.poi" % "poi-ooxml" % versionPoi)

  val _h2 = "com.h2database" % "h2" % "1.4.200"

  val _quartz = ("org.quartz-scheduler" % "quartz" % BuildInfo.versionQuartz).exclude("com.zaxxer", "HikariCP-java7")

  val _postgresql = "org.postgresql" % "postgresql" % "42.2.10"

  val _mysql = "mysql" % "mysql-connector-java" % "8.0.19"

  val _mssql = "com.microsoft.sqlserver" % "mssql-jdbc" % "8.2.0.jre11"

  val _commonsVfs = "org.apache.commons" % "commons-vfs2" % "2.2"

  val _commonsCompress = "org.apache.commons" % "commons-compress" % "1.20"

  val _commonsCodec = "commons-codec" % "commons-codec" % "1.14"

  val _jsch = "com.jcraft" % "jsch" % "0.1.55"

  val _jsoup = "org.jsoup" % "jsoup" % "1.12.2"
}
