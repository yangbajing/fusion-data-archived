package mass.job

import fusion.inject.guice.testkit.GuiceApplicationTestkit
import org.scalatest.wordspec.AnyWordSpecLike

class JobSchedulerTest extends GuiceApplicationTestkit with AnyWordSpecLike {
  private val jobScheduler = injectInstance[JobScheduler]

  "JobSystem" should {
    "toString" in {
      println(jobScheduler)
    }
  }
}
