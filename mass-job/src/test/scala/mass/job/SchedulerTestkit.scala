package mass.job

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.{ RouteTestTimeout, ScalatestRouteTest }
import com.typesafe.scalalogging.StrictLogging
import fusion.inject.guice.testkit.GuiceApplicationTestkit
import fusion.json.jackson.http.JacksonSupport
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._

abstract class SchedulerTestkit
    extends GuiceApplicationTestkit
    with AnyWordSpecLike
    with Matchers
    with BeforeAndAfterAll
    with ScalatestRouteTest
    with StrictLogging {
  override protected def createActorSystem(): ActorSystem = application.classicSystem
  implicit def routeTestTimeout: RouteTestTimeout = RouteTestTimeout(10.seconds)
  protected val jobScheduler: JobScheduler = injectInstance[JobScheduler]
  protected val jacksonSupport: JacksonSupport = injectInstance[JacksonSupport]
}
