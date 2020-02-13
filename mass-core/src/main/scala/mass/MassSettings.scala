/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass

import akka.actor.Address
import akka.actor.typed.ActorSystem
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import mass.core.Constants._

final class Compiles(c: Configuration) {
  def scala213Home: String = c.getString("scala213")
  def scala212Home: String = c.getString("scala212")
  def scala211Home: String = c.getString("scala211")
}

final class MassSettings private (val configuration: Configuration) extends StrictLogging {
  val compiles = new Compiles(configuration.getConfiguration(s"$BASE_CONF.core.compiles"))

  def clusterName: String = configuration.getString(BASE_CONF + ".cluster.name")

  def clusterProtocol: String = configuration.getString(BASE_CONF + ".cluster.protocol")

  def clusterSeeds: List[Address] =
    configuration
      .get[Seq[String]](BASE_CONF + ".cluster.seeds")
      .map { seed =>
        val Array(host, port) = seed.split(':')
        Address(clusterProtocol, clusterName, host, port.toInt)
      }
      .toList
}

object MassSettings {
  def apply(configuration: Configuration): MassSettings = new MassSettings(configuration)
  def apply(config: Config): MassSettings = apply(Configuration(config))
  def apply(system: ActorSystem[_]): MassSettings = apply(system.settings.config)
}
