package mass.job

import java.nio.file.{ Files, Path, Paths }

import mass.MassSettings
import mass.core.job.JobConstants
import mass.core.{ Constants, MassUtils }

case class JobSettings(massSettings: MassSettings) {
  private val configuration = massSettings.configuration

  private val conf = configuration.getConfiguration(s"${Constants.BASE_CONF}.job")

  val jobSavedDir: Path =
    conf.get[Option[Path]]("job-saved-dir").getOrElse(Paths.get(MassUtils.userDir, "share", "job-saved"))

  val jobRunDir: Path = Paths.get(MassUtils.userDir, "run", "job-run")

  if (!Files.isDirectory(jobSavedDir)) {
    Files.createDirectories(jobSavedDir)
  }
  if (!Files.isDirectory(jobRunDir)) {
    Files.createDirectories(jobRunDir)
  }

  def getJobRunDist(jobKey: String): Path = jobRunDir.resolve(jobKey).resolve(JobConstants.DIST)

  def schedulerRunJar: String = ""
}
