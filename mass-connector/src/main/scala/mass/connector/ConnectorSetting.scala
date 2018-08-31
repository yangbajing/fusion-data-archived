package mass.connector

import helloscala.common.Configuration

/**
 * 连接配置，包括但不限：
 * Source: JDBC
 */
case class ConnectorSetting(parameters: Configuration)
