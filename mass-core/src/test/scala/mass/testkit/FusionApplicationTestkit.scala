package mass.testkit

import akka.actor.typed.{ ActorRef, ActorSystem, Behavior, Props }
import akka.mass.AkkaUtils
import akka.stream.Materializer
import akka.{ actor => classic }
import com.typesafe.config.Config
import fusion.common.{ ReceptionistFactory, SpawnFactory }
import fusion.core.FusionApplication
import fusion.test.FusionScalaFutures
import helloscala.common.Configuration
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers
import org.scalatest.{ BeforeAndAfterAll, EitherValues, OptionValues, TestSuite }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

abstract class FusionApplicationTestkit(val application: FusionApplication)
    extends TestSuite
    with Matchers
    with BeforeAndAfterAll
    with FusionScalaFutures
    with Eventually
    with OptionValues
    with EitherValues
    with SpawnFactory
    with ReceptionistFactory {
  def this() = this(FusionApplication.start())

  def config: Config = application.config

  def configuration: Configuration = Configuration(config)

  implicit def materializer = Materializer.matFromSystem(application.classicSystem)

  implicit def executionContext: ExecutionContext = application.classicSystem.dispatcher

  override def typedSystem: ActorSystem[_] = application.typedSystem
  def classicSystem: classic.ActorSystem = application.classicSystem

  override def spawn[T](behavior: Behavior[T], props: Props): ActorRef[T] = application.spawn(behavior, props)

  override def spawn[T](behavior: Behavior[T], name: String, props: Props): ActorRef[T] =
    application.spawn(behavior, name, props)

  override protected def afterAll(): Unit = {
    AkkaUtils.shutdownActorSystem(application.classicSystem, 60.seconds)
  }
}
