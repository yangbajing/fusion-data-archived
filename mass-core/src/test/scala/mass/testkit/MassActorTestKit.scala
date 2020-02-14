package mass.testkit

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.mass.AkkaUtils
import com.typesafe.config.{ Config, ConfigFactory }
import fusion.common.{ FusionActorRefFactory, FusionProtocol }
import fusion.test.FusionScalaFutures
import mass.Mass
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.{ BeforeAndAfterAll, EitherValues, OptionValues, TestSuite }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

abstract class MassActorTestKit(val mass: Mass)
    extends TestSuite
    with Matchers
    with BeforeAndAfterAll
    with FusionScalaFutures
    with Eventually
    with OptionValues
    with EitherValues
    with FusionActorRefFactory {
  def this() = this(Mass.fromConfig(ConfigFactory.load("application-test.conf")))

  def this(resourceBasename: String) = this(Mass.fromConfig(ConfigFactory.load(resourceBasename)))

  def this(customConfig: Config) = this(Mass.fromConfig(customConfig))

  protected val system: ActorSystem[FusionProtocol.Command] = mass.system

  implicit def executionContext: ExecutionContext = system.executionContext

  override implicit def typedSystem: ActorSystem[_] = system

  override def fusionProtocolRef: ActorRef[FusionProtocol.Command] = system.narrow[FusionProtocol.Command]

  override def receptionistRef: ActorRef[Receptionist.Command] = system.receptionist

  override protected def afterAll(): Unit = {
    AkkaUtils.shutdownActorSystem(mass.system, 60.seconds)
  }
}
