/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.connector.boot

import com.typesafe.config.ConfigFactory
import mass.Global
import mass.connector.ConnectorSystem

object ConnectorMain extends App {
  val system = Global.registerActorSystem(ConfigFactory.load())
  ConnectorSystem(system)
}
