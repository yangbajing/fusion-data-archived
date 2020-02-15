package mass.job

import akka.actor.{ ActorSystem, typed }
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import fusion.common.FusionProtocol
import helloscala.common.Configuration
import mass.Mass
import mass.extension.MassSystem
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

abstract class SchedulerSpec(val mass: Mass)
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalatestRouteTest
    with StrictLogging {
  def this() = this(Mass.fromConfig(ConfigFactory.load("application-test.conf")))

  override protected def createActorSystem(): ActorSystem = mass.classicSystem

  private[this] var _jobSystem: JobScheduler = _

  protected def typedSystem: typed.ActorSystem[FusionProtocol.Command] = mass.system

  protected def jobSystem: JobScheduler = _jobSystem

  protected def massSystem: MassSystem = jobSystem.massSystem

  protected def configuration: Configuration = _jobSystem.configuration

  override protected def beforeAll(): Unit = {
    _jobSystem = JobScheduler(mass.system)
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
  }
}
