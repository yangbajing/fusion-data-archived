/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.connector.sql

import java.sql.PreparedStatement

import akka.stream.scaladsl.Sink
import fusion.jdbc.ConnectionPreparedStatementCreator
import fusion.jdbc.util.JdbcUtils
import javax.sql.DataSource

import scala.concurrent.Future

object JdbcSink {
  def apply(creator: ConnectionPreparedStatementCreator, args: Iterable[Any], batchSize: Int = 100)(
      implicit dataSource: DataSource): Sink[Iterable[Any], Future[JdbcSinkResult]] =
    apply(creator, (args, stmt) => JdbcUtils.setStatementParameters(stmt, args), batchSize)

  def apply[T](creator: ConnectionPreparedStatementCreator, action: (T, PreparedStatement) => Unit, batchSize: Int)(
      implicit dataSource: DataSource): Sink[T, Future[JdbcSinkResult]] =
    Sink.fromGraph(new JdbcSinkStage[T](dataSource, creator, action, batchSize))
}
