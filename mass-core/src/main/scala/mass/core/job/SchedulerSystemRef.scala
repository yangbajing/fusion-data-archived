package mass.core.job

import scala.concurrent.ExecutionContext

abstract class SchedulerSystemRef {
  val waitForJobsToComplete: Boolean
  implicit def executionContext: ExecutionContext
}
