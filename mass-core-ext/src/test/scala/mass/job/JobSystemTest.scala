package mass.job

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helloscala.common.test.HelloscalaSpec
import org.scalatest.BeforeAndAfterAll

class JobSystemTest extends TestKit(ActorSystem("mass")) with HelloscalaSpec with BeforeAndAfterAll {

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
