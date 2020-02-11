package mass

import akka.actor.ActorSystem
import com.typesafe.config.Config
import mass.core.Constants

object Global {
  private var _system: ActorSystem = _

  def registerActorSystem(config: Config): ActorSystem =
    registerActorSystem(config.getString(s"${Constants.BASE_CONF}.name"), config)

  def registerActorSystem(name: String, config: Config): ActorSystem = registerActorSystem(ActorSystem(name, config))

  def registerActorSystem(system: ActorSystem): ActorSystem = synchronized {
    require(_system eq null, "ActorSystem已设置")
    _system = system
    _system
  }

  def system: ActorSystem = synchronized {
    require(_system ne null, "ActorSystem未设置")
    _system
  }
}
