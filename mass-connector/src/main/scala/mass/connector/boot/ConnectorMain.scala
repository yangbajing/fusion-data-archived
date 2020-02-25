/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.connector.boot

import com.typesafe.config.ConfigFactory
import mass.Mass
import mass.connector.ConnectorSystem

object ConnectorMain extends App {
  ConnectorSystem(Mass.fromConfig(ConfigFactory.load()).classicSystem)
}
