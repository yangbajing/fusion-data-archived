package mass.rdp.etl.graph

import java.sql.PreparedStatement

import akka.NotUsed
import akka.stream.scaladsl.{ Sink, Source }
import fusion.jdbc.util.JdbcUtils
import mass.connector.Connector
import mass.connector.sql._
import mass.core.event.{ EventData, EventDataSimple }

import scala.concurrent.Future

trait EtlStreamFactory {
  def `type`: String

  def buildSource(c: Connector, s: EtlSource): Source[EventDataSql, NotUsed]

  def buildSink(c: Connector, s: EtlSink): Sink[EventData, Future[JdbcSinkResult]]
}

class EtlStreamJdbcFactory extends EtlStreamFactory {
  override def `type`: String = "jdbc"

  override def buildSource(c: Connector, s: EtlSource): Source[EventDataSql, NotUsed] =
    JdbcSource(s.script.content.get, Nil, 1000)(c.asInstanceOf[SQLConnector].dataSource)
      .via(JdbcFlow.flowJdbcResultSet)
      .map(jrs => EventDataSql(jrs))

  def buildSink(c: Connector, s: EtlSink): Sink[EventData, Future[JdbcSinkResult]] = {
    def action(event: EventData, stmt: PreparedStatement): Unit = {
      val args: Iterable[Any] = event match {
        case _: EventDataSimple         => event.data.asInstanceOf[Iterable[Any]]
        case eventDataSql: EventDataSql => eventDataSql.data.values
        case _                          => throw new EtlGraphException(s"Invalid EventData: $event.")
      }
      JdbcUtils.setStatementParameters(stmt, args)
    }
    JdbcSink[EventData](conn => conn.prepareStatement(s.script.content.get), action, 1000)(
      c.asInstanceOf[SQLConnector].dataSource)
  }
}
