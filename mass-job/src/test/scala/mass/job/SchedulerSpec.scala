package mass.job

import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.mass.AkkaUtils
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import mass.Global
import mass.extension.MassSystem
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._

trait SchedulerSpec
    extends AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ScalatestRouteTest
    with StrictLogging {
  private[this] var _massSystem: MassSystem = _
  private[this] var _jobSystem: JobSystem = _

  protected def massSystem: MassSystem = _massSystem

  protected def jobSystem: JobSystem = _jobSystem

  protected def configuration: Configuration = _jobSystem.configuration

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    Global.registerActorSystem(system.toTyped)
    _massSystem = MassSystem(system.toTyped)
    _jobSystem = JobSystem(system.toTyped)
  }

  override protected def afterAll(): Unit = {
    AkkaUtils.shutdownActorSystem(system, 10.seconds)
    super.afterAll()
  }
}
