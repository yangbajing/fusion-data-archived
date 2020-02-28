package mass.core.ext

import akka.actor.ActorSystem
import com.typesafe.config.Config
import fusion.common.config.FusionConfigFactory
import fusion.common.constant.FusionConstants
import mass.Mass

class MassApplication(val classicSystem: ActorSystem) {
  def this(config: Config) =
    this(MassApplication.createActorSystem(FusionConfigFactory.arrangeConfig(config, FusionConstants.FUSION)))

  val mass: Mass = Mass.fromActorSystem(classicSystem)
}

object MassApplication {
  def createActorSystem(config: Config): ActorSystem = {
    val name = config.getString("fusion.akka-name")
    ActorSystem(name, config)
  }
}
