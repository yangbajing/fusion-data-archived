/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.server

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import helloscala.common.util.Utils

import scala.collection.immutable.TreeMap
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.duration._

object MassBoot extends StrictLogging {
  object StartupType extends Enumeration {
    type StartupType = Value
    val // Identifies extensions
    EXTENSIONS, // Identifies actors as startup type
    ACTORS, // Identifies service as startup type
    SERVICES = Value
  }

  private[this] var _actorSystem: ActorSystem = _
  private[this] var _mat: ActorMaterializer = _

  def actorSystem: ActorSystem = {
    assert(_actorSystem ne null, "_actorSystem 在使用前必需先设置")
    _actorSystem // ActorSystem(config.getString("mass.cluster.name"), config)
  }

  def actorMaterializer: ActorMaterializer = {
    assert(_mat ne null, "_mat 在使用前必需先设置")
    _mat
  }

  def config: Config = actorSystem.settings.config

  def configuration: Configuration = Configuration(config)

  def init(config: Config): Unit =
    init(ActorSystem(Utils.getClusterName(config), config))

  def init(s: ActorSystem): Unit = {
    _actorSystem = s
    _mat = ActorMaterializer()(_actorSystem)

    val massConfig = new MassConfig(configuration)
    if (massConfig.clusterSeeds.nonEmpty) {
      Cluster(actorSystem).joinSeedNodes(massConfig.clusterSeeds)
    }

    logger.trace(startupDump())
  }

  def registerOnTermination[T](func: => T): Unit = {
    actorSystem.registerOnTermination(func)
  }

  def registerOnTermination(func: Runnable): Unit = {
    actorSystem.registerOnTermination(func)
  }

  def stop(): Unit = {
    actorSystem.terminate()
    Await
      .ready(actorSystem.whenTerminated, 60.seconds)
      .onComplete {
        case scala.util.Success(terminated) =>
          logger.info(s"MassServer退出成功：$terminated")
          System.exit(0)
        case scala.util.Failure(e) =>
          logger.error(s"MassServer退出错误：${e.getMessage}", e)
          System.exit(-1)
      }(Implicits.global)
  }

  def startupDump(): String = {
    val kvs = TreeMap(
      "akka.loglevel" -> config.getString("akka.loglevel"),
      "akka.stdout-loglevel" -> config.getString("akka.stdout-loglevel"),
      "akka.cluster.seed-nodes" -> config.getStringList(
        "akka.cluster.seed-nodes"),
      "akka.cluster.roles" -> config.getStringList("akka.cluster.roles"),
      "akka.remote.netty.tcp.hostname" -> config.getString(
        "akka.remote.netty.tcp.hostname"),
      "akka.remote.netty.tcp.port" -> config.getInt(
        "akka.remote.netty.tcp.port"),
      "akka.remote.artery.canonical.hostname" -> config.getString(
        "akka.remote.artery.canonical.hostname"),
      "akka.remote.artery.canonical.port" -> config.getInt(
        "akka.remote.netty.tcp.port")
    ) ++ configuration
      .get[Map[String, String]]("mass")
      .map(entry => ("mass." + entry._1, entry._2))
    kvs.map(entry => s"${entry._1} = ${entry._2}").mkString("\n")
  }

}
