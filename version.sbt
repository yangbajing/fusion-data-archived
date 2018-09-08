import java.nio.file.Files
import java.util.Properties

import com.typesafe.sbt.SbtGit.GitKeys.gitHeadCommit
import Environment._

version in ThisBuild := {
  val env = buildEnv.value
  val versionPath = baseDirectory.value
  val path = versionPath.toPath
  val props = new Properties()
  val file =
    if (Files.isRegularFile(path.resolve("version.properties"))) path.resolve("version.properties")
    else path.getParent.resolve("version.properties")
  props.load(Files.newInputStream(file))
  val ver = props.getProperty("VERSION")
  if (env == BuildEnv.Test) ver + "-" + gitHeadCommit.value.get
  else if (ver.endsWith("-SNAPSHOT")) ver
  else ver + "-SNAPSHOT"
}
