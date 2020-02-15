package mass.job

import mass.testkit.MassActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class JobSchedulerTest extends MassActorTestKit with AnyWordSpecLike {
  private val jobSystem: JobScheduler = JobScheduler(system)

  "JobSystem" should {
    "toString" in {
      println(jobSystem)
    }
  }
}
