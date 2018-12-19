package mass.core.job

import scala.concurrent.Future

// #SchedulerJob
trait SchedulerJob {
  def run(context: SchedulerContext): Future[JobResult]
}
// #SchedulerJob
