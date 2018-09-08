import sbt.Keys._
import sbt._
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.headerLicense
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.HeaderLicense

object Commons {

  import sbtassembly.AssemblyKeys.{assembly, assemblyMergeStrategy}
  import sbtassembly.{MergeStrategy, PathList}
  import Environment.{buildEnv, BuildEnv}

  def basicSettings =
    Seq(
      organization := "me.yangbajing",
      organizationName := "Yangbajing's Garden",
      organizationHomepage := Some(url("https://yangbajing.me")),
      homepage := Some(url("http://www.yangbajing.me/mass-data/doc/")),
      startYear := Some(2018),
      licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
      headerLicense := Some(HeaderLicense.ALv2("2018", "羊八井(yangbajing)（杨景）")),
      scalacOptions ++= {
        var list = Seq(
          "-encoding",
          "UTF-8", // yes, this is 2 args
          "-feature",
          "-deprecation",
          "-unchecked",
          "-Xlint",
          "-Yno-adapted-args", //akka-http heavily depends on adapted args and => Unit implicits break otherwise
          "-Ypartial-unification",
          "-opt:l:inline",
          "-opt-inline-from",
          "-Ywarn-dead-code"
        )
        if (scalaVersion.value.startsWith("2.12")) {
          list ++= Seq("-opt:l:inline", "-opt-inline-from")
        }
        if (buildEnv.value != BuildEnv.Developement) {
          list ++= Seq("-Xelide-below", "2001")
        }
        list
      },
      javacOptions in Compile ++= Seq("-Xlint:unchecked", "-Xlint:deprecation"),
      javaOptions in run ++= Seq("-Xms128m", "-Xmx1024m", "-Djava.library.path=./target/native"),
      shellPrompt := { s =>
        Project.extract(s).currentProject.id + " > "
      },
      test in assembly := {},
      assemblyMergeStrategy in assembly := {
        case PathList("javax", "servlet", xs @ _*)                => MergeStrategy.first
        case PathList("io", "netty", xs @ _*)                     => MergeStrategy.first
        case PathList("jnr", xs @ _*)                             => MergeStrategy.first
        case PathList("com", "datastax", xs @ _*)                 => MergeStrategy.first
        case PathList("com", "kenai", xs @ _*)                    => MergeStrategy.first
        case PathList("org", "objectweb", xs @ _*)                => MergeStrategy.first
        case PathList(ps @ _*) if ps.last endsWith ".html"        => MergeStrategy.first
        case "application.conf"                                   => MergeStrategy.concat
        case "META-INF/io.netty.versions.properties"              => MergeStrategy.first
        case PathList("org", "slf4j", xs @ _*)                    => MergeStrategy.first
        case "META-INF/native/libnetty-transport-native-epoll.so" => MergeStrategy.first
        case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      },
      //      resolvers ++= Seq(
      //      "elasticsearch-releases" at "https://artifacts.elastic.co/maven"
      //  )
      fork in run := true,
      fork in Test := true,
      parallelExecution in Test := false
    ) ++ Environment.settings // ++ Formatting.settings

}

object Publishing {

  import Environment._

  lazy val publishing = Seq(
    publishTo := (if (buildEnv.value == BuildEnv.Developement) {
                    Some(
                      "hualongdata-sbt-dev-local" at "https://artifactory.hualongdata.com/artifactory/sbt-dev-local;build.timestamp=" + new java.util.Date().getTime)
                  } else {
                    Some(
                      "hualongdata-sbt-release-local" at "https://artifactory.hualongdata.com/artifactory/sbt-release-local")
                  }),
    credentials += Credentials(Path.userHome / ".ivy2" / ".credentials_yangbajing")
  )

  lazy val noPublish = Seq(
    publish := ((): Unit),
    publishLocal := ((): Unit),
    publishTo := None
  )
}

object Environment {

  object BuildEnv extends Enumeration {
    val Production, Stage, Test, Developement = Value
  }

  val buildEnv = settingKey[BuildEnv.Value]("The current build environment")

  val settings = Seq(
    onLoadMessage := {
      // old message as well
      val defaultMessage = onLoadMessage.value
      val env = buildEnv.value
      s"""|$defaultMessage
          |Working in build environment: $env""".stripMargin
    }
  )
}

object Packaging {
  // Good example https://github.com/typesafehub/activator/blob/master/project/Packaging.scala
  import com.typesafe.sbt.SbtNativePackager._
  import com.typesafe.sbt.packager.Keys._
  import Environment.{buildEnv, BuildEnv}

  // This is dirty, but play has stolen our keys, and we must mimc them here.
  val stage = TaskKey[File]("stage")
  val dist = TaskKey[File]("dist")

  val settings = Seq(
    name in Universal := s"${name.value}",
    dist := (packageBin in Universal).value,
    mappings in Universal += {
      val confFile = buildEnv.value match {
        case BuildEnv.Developement => "dev.conf"
        case BuildEnv.Test         => "test.conf"
        case BuildEnv.Stage        => "stage.conf"
        case BuildEnv.Production   => "prod.conf"
      }
      (sourceDirectory(_ / "universal" / "conf").value / confFile) -> "conf/application.conf"
    },
    bashScriptExtraDefines ++= Seq(
      """addJava "-Dconfig.file=${app_home}/../conf/application.conf"""",
      """addJava "-Dpidfile.path=${app_home}/../run/%s.pid"""".format(name.value),
      """addJava "-Dlogback.configurationFile=${app_home}/../conf/logback.xml""""
    ),
    bashScriptConfigLocation := Some("${app_home}/../conf/jvmopts"),
    scriptClasspath := Seq("*"),
    mappings in (Compile, packageDoc) := Seq()
  )

}
