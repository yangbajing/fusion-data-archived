package mass.scheduler

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helloscala.common.test.HelloscalaSpec
import mass.core.MassSystem
import mass.server.MassSystemExtension
import org.scalatest.BeforeAndAfterAll

class SchedulerSystemTest extends TestKit(ActorSystem("mass")) with HelloscalaSpec with BeforeAndAfterAll {

  var schedulerSystem: SchedulerSystem = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    schedulerSystem = SchedulerSystem(MassSystem(system).as[MassSystemExtension])
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
  }

  "SchedulerSystem" should {
    "toString" in {
      println(schedulerSystem)
    }
  }

}
