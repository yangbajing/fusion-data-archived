/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.broker.boot

import akka.actor.{ActorRef, ActorSystem, PoisonPill}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import mass.broker.BrokerNode
import mass.broker.leader.BrokerLeader
import mass.core.Constants
import mass.server.{BaseBoot, MassBoot}

final class BrokerBoot(
    val system: ActorSystem,
    initBrokerLeaderProxy: Boolean = true
) extends BaseBoot {

  private[this] var _brokerNode: ActorRef = _

  def brokerNode: ActorRef = _brokerNode

  def start(): BrokerBoot = {
    MassBoot.init(system)
    system.actorOf(
      ClusterSingletonManager.props(
        singletonProps = BrokerLeader.props,
        terminationMessage = PoisonPill,
        settings = ClusterSingletonManagerSettings(system).withRole(Constants.Roles.BROKER)
      ),
      Constants.Nodes.BROKER_LEADER)

    if (initBrokerLeaderProxy) {
      startBrokerLeaderProxy()
    }

    _brokerNode = system.actorOf(BrokerNode.props, Constants.Nodes.BROKER)

    this
  }

}

object BrokerMain {

  def main(args: Array[String]): Unit = {
    new BrokerBoot(MassBoot.actorSystem).start()
  }

}
