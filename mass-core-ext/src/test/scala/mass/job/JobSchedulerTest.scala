package mass.job

import mass.testkit.FusionApplicationTestkit
import org.scalatest.wordspec.AnyWordSpecLike

class JobSchedulerTest extends FusionApplicationTestkit with AnyWordSpecLike {
  private val jobSystem: JobScheduler = JobScheduler(classicSystem)

  "JobSystem" should {
    "toString" in {
      println(jobSystem)
    }
  }
}
