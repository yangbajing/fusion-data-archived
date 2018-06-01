import sbt.Keys.version

object Version {

  import Environment._

  private def _version: String = sys.props.get("build.version")
    .orElse(sys.env.get("BUILD_VERSION"))
    .getOrElse("1.0.0")

  lazy val versionning = Seq(
    version := (if (buildEnv.value == BuildEnv.Developement && !_version.endsWith("-SNAPSHOT")) s"${_version}-SNAPSHOT" else _version)
  )
}
