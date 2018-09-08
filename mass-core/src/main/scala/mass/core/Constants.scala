/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.core

object Constants {
  val BASE_CONF = "mass"

  val SHA256_HEX_LENGTH = 64

  val STATUS_DISABLE = 0
  val STATUS_ENABLE = 1

  val OUT_LOG_SUFFIX = "out.log"
  val ERR_LOG_SUFFIX = "err.log"

  object Roles {
    val BROKER = "broker"
    val CONSOLE = "console"
  }

  object Nodes {
    val BROKER_LEADER = "broker-leader"

    val BROKER_LEADER_PROXY = "broker-leader-proxy"

    val BROKER = "mass-broker"

    val CONSOLE = "mass-console"
  }

}
