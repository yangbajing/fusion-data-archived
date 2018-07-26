package mass.scheduler

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import helloscala.common.test.HelloscalaSpec
import mass.core.MassSystem
import mass.server.MassSystemExtension
import org.scalatest.BeforeAndAfterAll

abstract class SchedulerSpec
    extends TestKit(ActorSystem("mass"))
    with HelloscalaSpec
    with BeforeAndAfterAll
    with StrictLogging {

  var schedulerSystem: SchedulerSystem = _

  protected def massSystem: MassSystemExtension = schedulerSystem.massSystem

  protected def configuration: Configuration = schedulerSystem.configuration

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    MassSystem(system)
    schedulerSystem = SchedulerSystem(MassSystemExtension.instance)
  }

}
