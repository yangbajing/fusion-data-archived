/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.connector.jdbc

import java.sql.{Connection, PreparedStatement}

import akka.stream.stage.{GraphStageLogic, GraphStageWithMaterializedValue, InHandler}
import akka.stream.{Attributes, Inlet, SinkShape}
import javax.sql.DataSource

import scala.concurrent.{Future, Promise}
import scala.util.control.NonFatal

class JdbcSinkStage[T](
    dataSource: DataSource,
    creator: ConnectionPreparedStatementCreator,
    actionBinder: (T, PreparedStatement) => Unit,
    batchSize: Int = 100
) extends GraphStageWithMaterializedValue[SinkShape[T], Future[JdbcSinkResult]] {
  val in: Inlet[T] = Inlet("JdbcSink.in")

  override def shape: SinkShape[T] = SinkShape(in)

  override def createLogicAndMaterializedValue(inheritedAttributes: Attributes): (GraphStageLogic, Future[JdbcSinkResult]) = {
    val promise = Promise[JdbcSinkResult]()

    val logic = new GraphStageLogic(shape) with InHandler {
      var count = 0
      var results: JdbcSinkResult = JdbcSinkResult(0L, Vector())
      var maybeConn = Option.empty[(Connection, Boolean, PreparedStatement)]

      setHandler(in, this)

      override def onPush(): Unit = {
        maybeConn match {
          case Some((_, _, stmt)) =>
            val v = grab(in)
            actionBinder(v, stmt)
            stmt.addBatch()

            count += 1
            if (count % batchSize == 0) {
              writeToDB()
            }
            pull(in)

          case None =>
            () // do nothing
        }
      }

      override def onUpstreamFinish(): Unit = {
        writeToDB()
        promise.trySuccess(results)
        completeStage()
      }

      override def onUpstreamFailure(e: Throwable): Unit = {
        setupFailure(e)
      }

      override def preStart(): Unit =
        try {
          val conn = dataSource.getConnection
          val autoCommit = conn.getAutoCommit
          conn.setAutoCommit(false)
          val stmt = creator(conn)
          maybeConn = Option((conn, autoCommit, stmt))
          pull(in)
        } catch {
          case NonFatal(e) =>
            setupFailure(e)
        }

      override def postStop(): Unit = {
        for {
          (conn, autoCommit, stmt) <- maybeConn
        } {
          JdbcUtils.closeStatement(stmt)
          conn.setAutoCommit(autoCommit)
          JdbcUtils.closeConnection(conn)
        }
      }

      private def writeToDB(): Unit =
        for {
          (conn, _, stmt) <- maybeConn if count > 0
        } {
          try {
            val batchs = stmt.executeBatch().toVector
            conn.commit()
            results = results.copy(count = results.count + batchs.size, results = results.results :+ batchs)
          } catch {
            case NonFatal(e) =>
              conn.rollback()
              setupFailure(e)
          }
        }

      private def setupFailure(e: Throwable): Unit = {
        promise.tryFailure(e)
        failStage(e)
      }
    }

    (logic, promise.future)
  }

}
