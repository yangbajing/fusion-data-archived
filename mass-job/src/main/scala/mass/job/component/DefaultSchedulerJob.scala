package mass.job.component

import java.nio.file.Paths

import com.typesafe.scalalogging.StrictLogging
import mass.core.job._
import mass.job.{ JobConstants, JobSystem }
import mass.message.job.SchedulerJobResult

import scala.concurrent.Future

class DefaultSchedulerJob extends SchedulerJob with StrictLogging {
  override def run(context: SchedulerContext): Future[JobResult] = {
    val jobSystem = JobSystem(context.system)
    context.jobItem.resources.get(JobConstants.Resources.ZIP_PATH).map(handleZip(_, jobSystem, context)) getOrElse
    handle(jobSystem, context)
  }

  private def handleZip(zipPath: String, jobSystem: JobSystem, ctx: SchedulerContext): Future[SchedulerJobResult] =
    Future {
      JobRun.runOnZip(Paths.get(zipPath), ctx.key, ctx.jobItem, jobSystem.jobSettings)
    }(jobSystem.executionContext)

  private def handle(jobSystem: JobSystem, ctx: SchedulerContext): Future[JobResult] =
    Future {
      JobRun.run(ctx.jobItem, ctx.key, jobSystem.jobSettings)
    }(jobSystem.executionContext)
}
