package mass.scheduler

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.mass.AkkaUtils
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import helloscala.common.test.HelloscalaSpec
import mass.core.MassSystem
import mass.server.MassSystemExtension
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration._

object TestAkkaSystem {
  val system = ActorSystem("mass")
}

trait SchedulerSpec extends HelloscalaSpec with BeforeAndAfterAll with ScalatestRouteTest with StrictLogging {

  protected val massSystem: MassSystemExtension =
    MassSystem(TestAkkaSystem.system).as[MassSystemExtension]

  override protected def createActorSystem(): ActorSystem =
    TestAkkaSystem.system

  private[this] var _schedulerSystem: SchedulerSystem = _

  protected def schedulerSystem: SchedulerSystem = _schedulerSystem

  protected def configuration: Configuration = schedulerSystem.configuration

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    _schedulerSystem = SchedulerSystem(massSystem)
  }

  override protected def afterAll(): Unit = {
    AkkaUtils.shutdownActorSystem(massSystem.system, 10.seconds)
    super.afterAll()
  }

}
