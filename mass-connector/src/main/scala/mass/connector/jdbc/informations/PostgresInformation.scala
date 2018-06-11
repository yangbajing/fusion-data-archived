package mass.connector.jdbc.informations

import helloscala.common.types.AsBoolean
import javax.sql.DataSource
import mass.connector.jdbc.{JdbcOperations, JdbcTemplate}

/**
 * PostgreSQL 表 Information
 * @param jdbcOperations Jdbc Template
 */
class PostgresInformation private (jdbcOperations: JdbcOperations) {

  def listTables(tableSchema: String = "public") = {
    jdbcOperations.listForMap(s"select * from information_schema.tables where table_schema = ?", List(tableSchema))
  }

  def listColumns(tableName: String, tableSchema: String = "public") = {
    jdbcOperations.listForMap("select * from information_schema.columns where table_schema = '?' and table_name = '?'", List(tableSchema, tableName))
  }

}

object PostgresInformation {
  class TableInfoPostgres(val _data: Map[String, AnyRef]) extends TableInfo {
    override def tableSchema: String = _data("table_schema").toString

    override def tableName: String = _data("table_name").toString

    override def tableType: String = _data("table_type").toString

    override def isInsertable: Boolean = AsBoolean.unapply(_data("is_insertable_into")).getOrElse(true)
  }

  class ColumnInfoPostgres(val _data: Map[String, AnyRef]) extends ColumnInfo {
    override def tableSchema: String = _data("table_schema").toString

    override def tableName: String = _data("table_name").toString

    override def columnName: String = _data("column_name").toString

    override def ordinalPosition: Int = asInt('ordinalPosition).get

    override def columnDefault: Option[String] = asString('columnDefault)

    override def isNullable: Boolean = asBoolean('isNullable).getOrElse(true)

    override def dataType: String = asString('dataType).get

    override def characterMaximumLength: Option[Int] = asInt('characterMaximumLength)

    override def characterOctetLength: Option[Int] = asInt('characterOctetLength)

    override def numericPrecision: Option[Int] = asInt('numericPrecision)

    override def numericPrecisionRadix: Option[Int] = asInt('numericPrecisionRadix)

    override def numericScale: Option[Int] = asInt('numericScale)

    override def datetimePrecision: Option[Int] = asInt('datetimePrecision)

    override def isUpdatable: Option[Boolean] = asBoolean('isUpdatable)
  }

  def apply(dataSource: DataSource): PostgresInformation = apply(JdbcTemplate(dataSource, true, true, false))
  def apply(jdbcOperations: JdbcOperations): PostgresInformation = new PostgresInformation(jdbcOperations)
}
