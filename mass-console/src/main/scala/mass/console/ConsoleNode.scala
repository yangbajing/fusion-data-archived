/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.console

import akka.actor.Props
import mass.core.server.MetricActor

object ConsoleNode {

  def props = Props(new ConsoleNode)

}

class ConsoleNode extends MetricActor {

  override def metricReceive: Receive = {
    case other => logger.debug(s"unknown msg: $other")
  }

}
