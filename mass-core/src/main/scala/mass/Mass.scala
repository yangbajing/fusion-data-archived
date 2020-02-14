package mass

import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.{ actor => classic }
import com.typesafe.config.Config
import fusion.common.config.FusionConfigFactory
import fusion.common.{ FusionActorRefFactory, FusionProtocol }
import helloscala.common.Configuration
import mass.core.Constants

final class Mass private (val system: ActorSystem[FusionProtocol.Command]) extends FusionActorRefFactory {
  def config: Config = system.settings.config
  def configuration: Configuration = Configuration(config)
  def classicSystem: classic.ActorSystem = system.toClassic

  override implicit def typedSystem: ActorSystem[_] = system

  override def fusionProtocolRef: ActorRef[FusionProtocol.Command] = system.narrow[FusionProtocol.Command]

  override def receptionistRef: ActorRef[Receptionist.Command] = system.receptionist
}

object Mass {
  def upcast(system: ActorSystem[_]) = new Mass(system.asInstanceOf[ActorSystem[FusionProtocol.Command]])

  def fromMergedConfig(config: Config): Mass =
    fromActorSystem(ActorSystem(apply(), Constants.MASS, config))

  private[mass] def fromActorSystem(system: ActorSystem[FusionProtocol.Command]): Mass = {
    Global.registerActorSystem(system)
    new Mass(system)
  }

  def fromConfig(originalConfig: Config): Mass = {
    val config = FusionConfigFactory.arrangeConfig(originalConfig, Constants.MASS, Seq("akka"))
    fromMergedConfig(config)
  }

  private def apply(): Behavior[FusionProtocol.Command] = FusionProtocol.behavior
}
