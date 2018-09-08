package mass.job

object JobConstants {
  private[mass] val JOB_CLASS = "JOB_CLASS"

  val DIST = "dist"
  val ENDS_SUFFIX = ".conf"

  object Resources {
    val ZIP_PATH = "ZIP_PATH"
  }

  object Roles {
    val CONTROL = "job-control"
    val AGENT = "job-agent"
  }
}
