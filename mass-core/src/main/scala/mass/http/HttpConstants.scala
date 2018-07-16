package mass.http

import mass.core.Constants

object HttpConstants {
  val CONFIG_PATH_PREFIX = s"${Constants.BASE_CONF}.akka-http"
  val HS_APP_ID = "hs-app-id"
  val HS_TIMESTAMP = "hs-timestamp"
  val HS_ECHO_STR = "hs-echo-str"
  val HS_ACCESS_TOKEN = "hs-access-token"
}
