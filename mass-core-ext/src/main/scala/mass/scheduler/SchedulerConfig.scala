package mass.scheduler

import java.nio.file.Path

import helloscala.common.Configuration

class SchedulerConfig(configuration: Configuration) {
  private val conf = configuration.getConfiguration("mass.scheduler")

  def jobSavedPath: Path = conf.get[Path]("job-saved-path")

}
