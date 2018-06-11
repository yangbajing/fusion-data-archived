package mass.connector.jdbc.informations

import helloscala.common.types._
import helloscala.common.util.StringUtils

trait BaseInfo {
  val _data: Map[String, AnyRef]

  def tableSchema: String

  def tableName: String

  protected def data(name: Symbol) = _data(StringUtils.convertPropertyToUnderscore(name.toString()))

  protected def data(name: String) = _data(name)

  @inline def asString(name: Symbol): Option[String] = asString(StringUtils.convertPropertyToUnderscore(name.toString()))

  def asString(name: String): Option[String] = _data.get(name).flatMap(AsString.unapply)

  @inline def asInt(name: Symbol): Option[Int] = asInt(StringUtils.convertPropertyToUnderscore(name.toString()))

  def asInt(name: String): Option[Int] = _data.get(name).flatMap(AsInt.unapply)

  @inline def asBoolean(name: Symbol): Option[Boolean] = asBoolean(StringUtils.convertPropertyToUnderscore(name.toString()))

  def asBoolean(name: String): Option[Boolean] = _data.get(name).flatMap(AsBoolean.unapply)

  @inline def asFloat(name: Symbol): Option[Float] = asFloat(StringUtils.convertPropertyToUnderscore(name.toString()))

  def asFloat(name: String): Option[Float] = _data.get(name).flatMap(AsFloat.unapply)

  @inline def asDouble(name: Symbol): Option[Double] = asDouble(StringUtils.convertPropertyToUnderscore(name.toString()))

  def asDouble(name: String): Option[Double] = _data.get(name).flatMap(AsDouble.unapply)
}
