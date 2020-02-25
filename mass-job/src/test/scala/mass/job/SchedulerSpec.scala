package mass.job

import akka.actor.{ ActorSystem, typed }
import akka.http.scaladsl.testkit.{ RouteTestTimeout, ScalatestRouteTest }
import com.typesafe.scalalogging.StrictLogging
import fusion.inject.GuiceApplication
import fusion.json.jackson.http.JacksonSupport
import helloscala.common.Configuration
import mass.extension.MassSystem
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._

abstract class SchedulerSpec(val application: GuiceApplication)
    extends AnyWordSpec
    with Matchers
    with BeforeAndAfterAll
    with ScalatestRouteTest
    with StrictLogging {
  def this() = this(new GuiceApplication())

  override protected def createActorSystem(): ActorSystem = application.classicSystem

  implicit def routeTestTimeout: RouteTestTimeout = RouteTestTimeout(10.seconds)

  private[this] var _jobScheduler: JobScheduler = _

  protected def typedSystem: typed.ActorSystem[_] = application.typedSystem

  protected def jobScheduler: JobScheduler = _jobScheduler

  protected def massSystem: MassSystem = jobScheduler.massSystem

  protected def configuration: Configuration = _jobScheduler.configuration

  lazy protected val jacksonSupport = application.instance[JacksonSupport]

  override protected def beforeAll(): Unit = {
    _jobScheduler = JobScheduler(application.classicSystem)
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
  }
}
