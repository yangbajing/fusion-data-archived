package mass.core.job

import scala.concurrent.ExecutionContext

trait SchedulerSystemRef {
  val waitForJobsToComplete: Boolean
  implicit def executionContext: ExecutionContext
}
