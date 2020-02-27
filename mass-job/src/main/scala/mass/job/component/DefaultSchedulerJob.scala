package mass.job.component

import java.nio.file.Paths

import com.typesafe.scalalogging.StrictLogging
import fusion.inject.guice.GuiceApplication
import mass.core.job._
import mass.job.JobScheduler
import mass.message.job.SchedulerJobResult

import scala.concurrent.Future

class DefaultSchedulerJob extends SchedulerJob with StrictLogging {
  override def run(context: SchedulerContext): Future[JobResult] = {
    val jobScheduler = GuiceApplication(context.system).instance[JobScheduler]
    // TODO Use job blocking dispatcher
    val blockingDispatcher = jobScheduler.executionContext
    Future {
      context.jobItem.resources.get(JobConstants.Resources.ZIP_PATH) match {
        case Some(zipPath) => handleZip(zipPath, jobScheduler, context)
        case _             => handle(jobScheduler, context)
      }
    }(blockingDispatcher)
  }

  private def handleZip(zipPath: String, jobSystem: JobScheduler, ctx: SchedulerContext): SchedulerJobResult =
    JobRun.runOnZip(Paths.get(zipPath), ctx.key, ctx.jobItem, jobSystem.jobSettings)

  private def handle(jobSystem: JobScheduler, ctx: SchedulerContext): SchedulerJobResult =
    JobRun.run(ctx.jobItem, ctx.key, jobSystem.jobSettings)
}
