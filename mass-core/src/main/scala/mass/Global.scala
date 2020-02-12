package mass

import akka.actor.typed.{ ActorSystem, SpawnProtocol }
import com.typesafe.config.Config
import mass.core.Constants

object Global {
  private var _system: ActorSystem[_] = _

  def registerActorSystem(config: Config): ActorSystem[_] =
    registerActorSystem(config.getString(s"${Constants.BASE_CONF}.name"), config)

  def registerActorSystem(name: String, config: Config): ActorSystem[_] =
    registerActorSystem(ActorSystem(SpawnProtocol(), "fusion-mass")) // TODO

  def registerActorSystem(system: ActorSystem[_]): ActorSystem[_] = synchronized {
    require(_system eq null, "ActorSystem已设置")
    _system = system
    _system
  }

  def system: ActorSystem[_] = synchronized {
    require(_system ne null, "ActorSystem未设置")
    _system
  }
}
