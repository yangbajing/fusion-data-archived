package mass.connector.sql.schema

import helloscala.common.types._
import helloscala.common.util.StringUtils

import scala.collection.immutable

trait SQLSchema {
  def listTable(schemaName: String): immutable.Seq[TableInfo]

  def listColumn(tableName: String, schemaName: String): immutable.Seq[ColumnInfo]
}

trait BaseInfo {
  def schemaName: String

  def tableName: String
}

abstract class InfoHelper(_data: Map[String, AnyRef]) {
  protected def data(name: Symbol) = _data(StringUtils.convertPropertyToUnderscore(name.name))

  protected def data(name: String) = _data(name)

  @inline def asString(name: Symbol): Option[String] = asString(StringUtils.convertPropertyToUnderscore(name.name))

  def asString(name: String): Option[String] = _data.get(name).flatMap(AsString.unapply)

  @inline def asInt(name: Symbol): Option[Int] = asInt(StringUtils.convertPropertyToUnderscore(name.name))

  def asInt(name: String): Option[Int] = _data.get(name).flatMap(AsInt.unapply)

  @inline def asBoolean(name: Symbol): Option[Boolean] = asBoolean(StringUtils.convertPropertyToUnderscore(name.name))

  def asBoolean(name: String): Option[Boolean] = _data.get(name).flatMap(AsBoolean.unapply)

  @inline def asFloat(name: Symbol): Option[Float] = asFloat(StringUtils.convertPropertyToUnderscore(name.name))

  def asFloat(name: String): Option[Float] = _data.get(name).flatMap(AsFloat.unapply)

  @inline def asDouble(name: Symbol): Option[Double] = asDouble(StringUtils.convertPropertyToUnderscore(name.name))

  def asDouble(name: String): Option[Double] = _data.get(name).flatMap(AsDouble.unapply)
}

case class ColumnInfo(
    schemaName: String,
    tableName: String,
    columnName: String,
    ordinalPosition: Int,
    columnDefault: Option[String],
    isNullable: Boolean,
    dataType: String,
    characterMaximumLength: Option[Int],
    characterOctetLength: Option[Int],
    numericPrecision: Option[Int],
    numericPrecisionRadix: Option[Int],
    numericScale: Option[Int],
    datetimePrecision: Option[Int],
    isUpdatable: Option[Boolean]) extends BaseInfo

case class TableInfo(
    schemaName: String,
    tableName: String,
    tableType: String,
    isInsertable: Boolean) extends BaseInfo
