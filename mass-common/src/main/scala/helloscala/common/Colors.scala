/*
 * Copyright 2018 羊八井(yangbajing)（杨景）
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helloscala.common

/**
 * Created by yangbajing(yangbajing@gmail.com) on 2017-02-21.
 */
object Colors {

  import scala.Console._

  lazy val isANSISupported: Boolean = {
    Option(System.getProperty("sbt.log.noformat"))
      .map(_ != "true")
      .orElse {
        Option(System.getProperty("os.name"))
          .map(_.toLowerCase(java.util.Locale.ENGLISH))
          .filter(_.contains("windows"))
          .map(_ => false)
      }
      .getOrElse(true)
  }

  def red(str: String): String = if (isANSISupported) RED + str + RESET else str

  def blue(str: String): String =
    if (isANSISupported) BLUE + str + RESET else str

  def cyan(str: String): String =
    if (isANSISupported) CYAN + str + RESET else str

  def green(str: String): String =
    if (isANSISupported) GREEN + str + RESET else str

  def magenta(str: String): String =
    if (isANSISupported) MAGENTA + str + RESET else str

  def white(str: String): String =
    if (isANSISupported) WHITE + str + RESET else str

  def black(str: String): String =
    if (isANSISupported) BLACK + str + RESET else str

  def yellow(str: String): String =
    if (isANSISupported) YELLOW + str + RESET else str

}
