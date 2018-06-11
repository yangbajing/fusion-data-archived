/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.console.boot

import mass.core.server.MassBoot

object ConsoleMain extends App {
  new ConsoleBoot(MassBoot.actorSystem).start()
}
