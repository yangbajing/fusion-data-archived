package mass.rdp.boot

import com.typesafe.config.ConfigFactory
import mass.Global
import mass.rdp.RdpSystem

object RdpMain extends App {
  val system = Global.registerActorSystem(ConfigFactory.load())
  RdpSystem(system)
}
