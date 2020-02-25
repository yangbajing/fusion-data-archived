package mass.rdp.boot

import com.typesafe.config.ConfigFactory
import mass.Mass
import mass.rdp.RdpSystem

object RdpMain extends App {
  RdpSystem(Mass.fromConfig(ConfigFactory.load()).classicSystem)
}
