package mass

import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior, Props }
import akka.{ actor => classic }
import com.typesafe.config.Config
import fusion.common.config.FusionConfigFactory
import fusion.common.{ ReceptionistFactory, SpawnFactory }
import fusion.core.extension.FusionCore
import helloscala.common.Configuration
import mass.core.Constants

import scala.concurrent.ExecutionContext

final class Mass private (val classicSystem: classic.ActorSystem) extends SpawnFactory with ReceptionistFactory {
  implicit def executionContext: ExecutionContext = classicSystem.dispatcher

  val configuration: Configuration = FusionCore(classicSystem).configuration

  override def typedSystem: ActorSystem[_] = classicSystem.toTyped

  override def spawn[T](behavior: Behavior[T], props: Props): ActorRef[T] =
    classicSystem.spawnAnonymous(behavior, props)

  override def spawn[T](behavior: Behavior[T], name: String, props: Props): ActorRef[T] =
    classicSystem.spawn(behavior, name, props)
}

object Mass {
  def fromMergedConfig(config: Config): Mass =
    fromActorSystem(classic.ActorSystem(Constants.MASS, config))

  private[mass] def fromActorSystem(system: classic.ActorSystem): Mass = new Mass(system)

  def fromConfig(originalConfig: Config): Mass = {
    val config = FusionConfigFactory.arrangeConfig(originalConfig, Constants.MASS, Seq("akka"))
    fromMergedConfig(config)
  }
}
