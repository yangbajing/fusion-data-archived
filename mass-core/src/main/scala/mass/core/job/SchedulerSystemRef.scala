package mass.core.job

import scala.concurrent.ExecutionContext

abstract class SchedulerSystemRef {
  val name: String
  val waitForJobsToComplete: Boolean

  implicit def dispatcher: ExecutionContext
}
