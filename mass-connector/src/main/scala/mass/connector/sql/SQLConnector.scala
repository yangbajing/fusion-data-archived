package mass.connector.sql

import com.zaxxer.hikari.HikariDataSource
import mass.connector.ConnectorType.ConnectorType
import mass.connector.{Connector, ConnectorSetting, ConnectorType}
import mass.core.jdbc.{JdbcTemplate, JdbcUtils}

/**
 *
 */
final case class SQLConnector(name: String, setting: ConnectorSetting) extends Connector {

  override def `type`: ConnectorType = ConnectorType.JDBC

  override type SourceType = JdbcResultSet
  override type SinkType = JdbcSinkResult

  override def createSource: SourceType = ???

  override def createSink: SinkType = ???

  lazy val dataSource: HikariDataSource = JdbcUtils.createHikariDataSource(configuration)
  lazy val jdbcTemplate = JdbcTemplate(
    dataSource,
    configuration.get[Option[Boolean]]("use-transaction").getOrElse(true),
    configuration.get[Option[Boolean]]("ignore-warnings").getOrElse(true),
    configuration.get[Option[Boolean]]("allow-print-log").getOrElse(false))

  override def close(): Unit = dataSource.close()
}
