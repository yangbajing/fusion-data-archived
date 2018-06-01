/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package helloscala.common.data

case class NameValue(name: String, value: String)

object NameValue {
  def apply(name: String, values: Traversable[String]): NameValue = NameValue(name, values.mkString(", "))
}
