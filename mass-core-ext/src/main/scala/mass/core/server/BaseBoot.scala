/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.core.server

import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}
import com.typesafe.scalalogging.StrictLogging
import mass.core.Constants

trait BaseBoot extends StrictLogging {
  val system: ActorSystem

  private[this] var _brokerLeaderProxy: ActorRef = _

  def brokerLeaderProxy: ActorRef = _brokerLeaderProxy

  protected def startBrokerLeaderProxy(): Unit = {
    _brokerLeaderProxy = system.actorOf(
      ClusterSingletonProxy.props(
        s"/user/${Constants.Nodes.BROKER_LEADER}",
        ClusterSingletonProxySettings(system).withRole(Constants.Roles.BROKER)),
      Constants.Nodes.BROKER_LEADER_PROXY)

    logger.info(s"broker单例代理 ${_brokerLeaderProxy.path} 已启动")
  }

}
