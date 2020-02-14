package mass

import akka.actor.typed.ActorSystem
import fusion.json.jackson.Jackson

object Global {
  private var _system: ActorSystem[_] = _

  private[mass] def registerActorSystem(system: ActorSystem[_]): ActorSystem[_] = synchronized {
    if (_system != null) {
      throw new ExceptionInInitializerError("ActorSystem[_] already set.")
    }
//    Jackson.defaultObjectMapper.findAndRegisterModules()
    _system = system
    _system
  }

  def system: ActorSystem[_] = synchronized {
    if (_system == null) {
      throw new ExceptionInInitializerError("ActorSystem[_] not set.")
    }
    _system
  }
}
