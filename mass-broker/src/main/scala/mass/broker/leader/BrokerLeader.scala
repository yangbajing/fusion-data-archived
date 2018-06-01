/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.broker.leader

import akka.actor.Props
import akka.cluster.Cluster
import mass.core.server.MetricActor

object BrokerLeader {

  //  case object End

  def props: Props = Props(new BrokerLeader)
}

class BrokerLeader extends MetricActor {

  override def preStart(): Unit = {
    logger.info(s"引擎Leader已启动: ${Cluster(context.system).selfAddress} $self")
  }

  override def postStop(): Unit = {
    logger.info(s"引擎Leader已停止：${self.path.address} $self")
  }

  override def metricReceive: Receive = {
    case other => logger.info(s"unknown msg: $other")
  }

}
