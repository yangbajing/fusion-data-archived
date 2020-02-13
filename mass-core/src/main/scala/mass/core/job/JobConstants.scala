package mass.core.job

import scala.concurrent.duration._

object JobConstants {
  private[mass] val JOB_CLASS = "JOB_CLASS"

  val TRIGGER_REPEAT = 0
  val TRIGGER_INTERVAL: FiniteDuration = 1.minutes
  val RUN_TIMEOUT: FiniteDuration = 2.hours

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
