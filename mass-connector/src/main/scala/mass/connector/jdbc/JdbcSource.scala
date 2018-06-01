/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.connector.jdbc

import java.sql.ResultSet

import akka.NotUsed
import akka.stream.scaladsl.Source
import javax.sql.DataSource

object JdbcSource {

  def apply(sql: String, args: Iterable[Any], fetchRowSize: Int)(implicit dataSource: DataSource): Source[ResultSet, NotUsed] =
    Source.fromGraph(new JdbcSourceStage(
      dataSource,
      conn => {
        val stmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
        JdbcUtils.setStatementParameters(stmt, args)
      },
      fetchRowSize))

  def apply(creator: ConnectionPreparedStatementCreator, fetchRowSize: Int)(implicit dataSource: DataSource): Source[ResultSet, NotUsed] =
    Source.fromGraph(new JdbcSourceStage(dataSource, creator, fetchRowSize))

}
