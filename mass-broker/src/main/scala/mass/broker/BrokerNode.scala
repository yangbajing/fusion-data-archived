/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.broker

import akka.actor.Props
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import mass.server.MetricActor

object BrokerNode {

  def props: Props = Props(new BrokerNode)

}

class BrokerNode extends MetricActor {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self,
                      InitialStateAsEvents,
                      classOf[MemberEvent],
                      classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def metricReceive: Receive = {
    case MemberUp(member) =>
    //      logger.info("Member is Up: {}, roles: {}", member, member.roles)
    case UnreachableMember(member) =>
    //      logger.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
    //      logger.info("Member is Removed: {} after {}", member, previousStatus)
    case _: MemberEvent => // ignore
  }

}
