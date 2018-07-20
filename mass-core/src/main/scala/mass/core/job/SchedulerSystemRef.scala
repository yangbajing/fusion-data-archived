package mass.core.job

import mass.core.BaseSystem

import scala.concurrent.ExecutionContext

abstract class SchedulerSystemRef extends BaseSystem {
  val waitForJobsToComplete: Boolean

  implicit def dispatcher: ExecutionContext
}
