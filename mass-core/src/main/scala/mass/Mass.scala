package mass

import akka.actor.typed.scaladsl.adapter._
import akka.actor.typed.{ ActorSystem, Behavior }
import akka.{ actor => classic }
import com.typesafe.config.Config
import fusion.common.config.FusionConfigFactory
import fusion.common.{ FusionActorRefFactory, FusionProtocol }
import helloscala.common.Configuration
import mass.core.Constants

final class Mass(val system: ActorSystem[FusionProtocol.Command]) extends FusionActorRefFactory {
  def config: Config = system.settings.config
  def configuration: Configuration = Configuration(config)
  def classicSystem: classic.ActorSystem = system.toClassic
}

object Mass {
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
