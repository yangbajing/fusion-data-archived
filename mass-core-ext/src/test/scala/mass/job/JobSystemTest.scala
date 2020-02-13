package mass.job

import mass.testkit.MassActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class JobSystemTest extends MassActorTestKit with AnyWordSpecLike {
  private val jobSystem: JobSystem = JobSystem(system)

  "JobSystem" should {
    "toString" in {
      println(jobSystem)
    }
  }
}
