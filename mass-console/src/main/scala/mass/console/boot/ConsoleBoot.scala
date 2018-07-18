/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.console.boot

import akka.actor.{ActorRef, ActorSystem}
import mass.console.ConsoleNode
import mass.core.Constants
import mass.server.{BaseBoot, MassBoot}

final class ConsoleBoot(
    val system: ActorSystem,
    initBrokerLeaderProxy: Boolean = true
) extends BaseBoot {
  private[this] var consoleNode: ActorRef = _

  def start(): ConsoleBoot = {
    MassBoot.init(system)
    consoleNode = system.actorOf(ConsoleNode.props, Constants.Nodes.CONSOLE)

    this
  }

}

