package mass.job

import java.nio.file.{ Path, Paths }

import mass.core.Constants
import mass.server.MassSettings

case class JobSettings(massSettings: MassSettings) {
  private val configuration = massSettings.configuration

  private val conf = configuration.getConfiguration(s"${Constants.BASE_CONF}.job")

  def jobSavedDir: Path =
    conf
      .get[Option[Path]]("job-saved-dir")
      .getOrElse(Paths.get(sys.props.getOrElse("user.dir", ""), "share", "job-saved"))

  def jobRunDir: Path = Paths.get(sys.props.getOrElse("user.dir", ""), "share", "job-run")

  def getJobRunDist(jobKey: String): Path = jobRunDir.resolve(jobKey).resolve(JobConstants.DIST)

  def schedulerRunJar: String = ""
}
