package mass.core.model.scheduler

import java.nio.file.{Files, Paths}

import mass.core.job.JobResult

trait SchedulerJobResultTrait extends JobResult {
  val outPath: String
  val errPath: String

  def destroy(): Unit = {
    Files.deleteIfExists(Paths.get(outPath))
    Files.deleteIfExists(Paths.get(errPath))
  }
}
