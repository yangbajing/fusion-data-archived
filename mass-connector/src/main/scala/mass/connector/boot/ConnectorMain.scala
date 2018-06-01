/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.connector.boot

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object ConnectorMain extends App {
  println(LocalDateTime.now().truncatedTo(ChronoUnit.HOURS))
}
