/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.server

import akka.actor.Address
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import mass.core.Constants._

case class MassSettings(configuration: Configuration) extends StrictLogging {
  if (!configuration.hasPath(s"$BASE_CONF.core.scala212-home")) {
    logger.warn(s"未配置Scala 2.12主目录，config key: $BASE_CONF.core.scala212-home")
  }
  if (!configuration.hasPath(s"$BASE_CONF.core.scala211-home")) {
    logger.info(s"未配置Scala 2.11主目录，config key: $BASE_CONF.core.scala211-home")
  }

  def scala212Home: String = configuration.getOrElse[String](s"$BASE_CONF.core.scala212-home", "")

  def scala211Home: String = configuration.getOrElse[String](s"$BASE_CONF.core.scala211-home", "")

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
