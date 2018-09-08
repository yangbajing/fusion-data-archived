package mass.connector.sql.schema

import helloscala.common.types.AsBoolean
import javax.sql.DataSource
import mass.core.jdbc.{JdbcTemplate, JdbcUtils}

import scala.collection.immutable
import scala.util.control.NonFatal

/**
 * PostgreSQL 表 Information
 * TODO Refactor
 * @param jdbcTemplate Jdbc Template
 */
class PostgresSchema private (jdbcTemplate: JdbcTemplate) extends SQLSchema {

  import mass.connector.sql.schema.PostgresSchema._

  override def listTable(schemaName: String): immutable.Seq[TableInfo] =
    jdbcTemplate.listForObject(s"select * from information_schema.tables where table_schema = ?",
                               List(schemaName),
                               rs => tableInfo(jdbcTemplate, JdbcUtils.resultSetToMap(rs)))

  override def listColumn(tableName: String, schemaName: String): immutable.Seq[ColumnInfo] =
    jdbcTemplate.listForObject(
      "select * from information_schema.columns where table_schema = ? and table_name = ?",
      List(schemaName, tableName),
      rs => columnInfo(JdbcUtils.resultSetToMap(rs))
    )

}

object PostgresSchema {

  def listColumn(jdbcTemplate: JdbcTemplate, tableName: String, schemaName: String): immutable.Seq[ColumnInfo] =
    jdbcTemplate.listForObject(
      "select * from information_schema.columns where table_schema = '?' and table_name = '?'",
      List(schemaName, tableName),
      rs => columnInfo(JdbcUtils.resultSetToMap(rs))
    )

  def tableInfo(jdbcTemplate: JdbcTemplate, _data: Map[String, AnyRef]): TableInfo =
    TableInfo(
      _data("table_schema").toString,
      _data("table_name").toString,
      _data("table_type").toString,
      AsBoolean.unapply(_data("is_insertable_into")).getOrElse(true)
    )

  def columnInfo(_data: Map[String, AnyRef]): ColumnInfo = {
    val helper = new InfoHelper(_data) {}
    try {
      ColumnInfo(
        _data("table_schema").toString,
        _data("table_name").toString,
        _data("column_name").toString,
        helper.asInt('ordinalPosition).get,
        helper.asString("column_default"),
        helper.asBoolean('isNullable).getOrElse(true),
        helper.asString("data_type").getOrElse(""),
        helper.asInt('characterMaximumLength),
        helper.asInt('characterOctetLength),
        helper.asInt('numericPrecision),
        helper.asInt('numericPrecisionRadix),
        helper.asInt('numericScale),
        helper.asInt('datetimePrecision),
        helper.asBoolean('isUpdatable)
      )
    } catch {
      case NonFatal(e) =>
        println(_data("data_type"))
        println(_data)
        e.printStackTrace()
        throw e
    }
  }

  def apply(dataSource: DataSource): PostgresSchema =
    apply(JdbcTemplate(dataSource, true, true, false))

  def apply(JdbcTemplate: JdbcTemplate): PostgresSchema =
    new PostgresSchema(JdbcTemplate)
}
