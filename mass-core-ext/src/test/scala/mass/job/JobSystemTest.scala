package mass.job

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class JobSystemTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  var jobSystem: JobSystem = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    jobSystem = JobSystem(system)
  }

  override protected def afterAll(): Unit =
    super.afterAll()

  "SchedulerSystem" should {
    "toString" in {
      println(jobSystem)
    }
  }
}
