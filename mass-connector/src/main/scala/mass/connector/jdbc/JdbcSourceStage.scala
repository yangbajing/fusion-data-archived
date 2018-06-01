/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.connector.jdbc

import java.sql.{Connection, PreparedStatement, ResultSet}

import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}
import javax.sql.DataSource

import scala.util.control.NonFatal

class JdbcSourceStage(
    dataSource: DataSource,
    creator: ConnectionPreparedStatementCreator,
    fetchRowSize: Int
) extends GraphStage[SourceShape[ResultSet]] {

  private val out: Outlet[ResultSet] = Outlet("JdbcSource.out")

  override def shape: SourceShape[ResultSet] = SourceShape(out)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with OutHandler {
      var maybeConn = Option.empty[(Connection, Boolean, PreparedStatement, ResultSet)]

      setHandler(out, this)

      override def onPull(): Unit = {
        maybeConn match {
          case Some((_, _, _, rs)) if rs.next() =>
            push(out, rs)
          case Some(_) =>
            completeStage()
          case None =>
            () // doing nothing, waiting for in preStart() to be completed
        }
      }

      override def preStart(): Unit =
        try {
          val conn = dataSource.getConnection
          val autoCommit = conn.getAutoCommit
          conn.setAutoCommit(false)
          val stmt = creator(conn)
          val rs = stmt.executeQuery()
          //          rs.setFetchDirection(ResultSet.TYPE_FORWARD_ONLY)
          rs.setFetchSize(fetchRowSize)
          maybeConn = Option((conn, autoCommit, stmt, rs))
        } catch {
          case NonFatal(e) => failStage(e)
        }

      override def postStop(): Unit =
        for {
          (conn, autoCommit, stmt, rs) <- maybeConn
        } {
          JdbcUtils.closeResultSet(rs)
          JdbcUtils.closeStatement(stmt)
          conn.setAutoCommit(autoCommit)
          JdbcUtils.closeConnection(conn)
        }
    }

}
