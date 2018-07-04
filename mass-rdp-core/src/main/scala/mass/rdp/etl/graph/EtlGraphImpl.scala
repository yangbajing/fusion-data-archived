package mass.rdp.etl.graph

import akka.NotUsed
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.scalalogging.StrictLogging
import javax.script.SimpleBindings
import mass.connector.Connector
import mass.connector.sql._
import mass.core.event.{EventData, EventDataSimple}
import mass.core.script.ScriptManager
import mass.rdp.RdpSystem
import mass.rdp.etl.{EtlResult, EtlWorkflowExecution, SqlEtlResult}

import scala.collection.immutable
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

case class EtlGraphImpl(graphSetting: EtlGraphSetting) extends EtlGraph with StrictLogging {

  override def run(connectors: immutable.Seq[Connector], rdpSystem: RdpSystem): EtlWorkflowExecution = {
    implicit val ec = rdpSystem.materializer.system.dispatcher
    implicit val mat = rdpSystem.materializer

    def getConnector(name: String): Connector =
      connectors.find(_.name == name) orElse
        rdpSystem.connectorSystem.getConnector(name) getOrElse
        (throw new EtlGraphException(s"connector ref: $name 不存在"))

    val promise = Promise[EtlResult]()

    val source = dataSource(getConnector(graphSource.connector.ref), rdpSystem)
    val sink = dataSink(getConnector(graphSink.connector.ref), rdpSystem)

    graphFlows
      .foldLeft(source)((s, etlFlow) =>
        s.map { event =>
          val engine = ScriptManager.scriptJavascript
          val bindings = new SimpleBindings()
          bindings.put("event", event.asInstanceOf[EventDataSql])
          val data = engine.eval(etlFlow.script.content.get, bindings)

          // TODO 在此可设置是否发送通知消息给在线监控系统
          logger.debug(s"engine: $engine, event: $event, result data: $data")

          EventDataSimple(data)
        }
      )
      .runWith(sink)
      .onComplete {
        case Success(result) => promise.success(SqlEtlResult(result))
        case Failure(e)      => promise.failure(e)
      }

    new EtlWorkflowExecution(promise, () => ())
  }

  private def dataSource(connector: Connector, rdpSystem: RdpSystem): Source[EventData, NotUsed] =
    rdpSystem.streamFactories.get(connector.`type`.toString) match {
      case Some(b) => b.buildSource(connector, graphSource)
      case _       => throw new EtlGraphException(s"未知Connector: $connector")
    }

  private def dataSink(connector: Connector, rdpSystem: RdpSystem): Sink[EventData, Future[JdbcSinkResult]] =
    rdpSystem.streamFactories.get(connector.`type`.toString) match {
      case Some(b) => b.buildSink(connector, graphSink)
      case _       => throw new EtlGraphException(s"未知Connector: $connector")
    }

}
