/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.core.jdbc

import java.sql.{Connection, PreparedStatement}

import helloscala.common.util.StringUtils

@FunctionalInterface
trait ConnectionPreparedStatementCreator {
  def apply(conn: Connection): PreparedStatement
}

class ConnectionPreparedStatementCreatorImpl(sql: String, namedSql: String = "")
    extends ConnectionPreparedStatementCreator {
  def getSql: String = if (StringUtils.isNoneBlank(namedSql)) namedSql else sql

  override def apply(conn: Connection): PreparedStatement = conn.prepareStatement(sql)
}
