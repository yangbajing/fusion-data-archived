/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.server

import akka.actor.Address
import helloscala.common.Configuration
import mass.core.Constants

class MassConfig(configuration: Configuration) {

  def clusterName: String =
    configuration.getString(Constants.BASE_CONF + ".cluster.name")

  def clusterProtocol: String =
    configuration.getString(Constants.BASE_CONF + ".cluster.protocol")

  def clusterSeeds: List[Address] =
    configuration
      .get[Seq[String]](Constants.BASE_CONF + ".cluster.seeds")
      .map { seed =>
        val Array(host, port) = seed.split(':')
        Address(clusterProtocol, clusterName, host, port.toInt)
      }
      .toList

}
