package mass.connector

import java.nio.file.Path

import akka.Done
import akka.actor.typed.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import fusion.common.extension.{ FusionExtension, FusionExtensionId }
import fusion.core.extension.FusionCore
import mass.core.Constants

import scala.concurrent.Future
import scala.util.{ Failure, Success }

final class ConnectorSystem private (val system: ActorSystem[_]) extends FusionExtension with StrictLogging {
  private var _parsers = Map.empty[String, ConnectorParser]
  private var _connectors = Map.empty[String, Connector]

  init()

  private def init(): Unit = {
    configuration.get[Seq[String]](s"${Constants.BASE_CONF}.connector.parsers").foreach { className =>
      system.dynamicAccess.createInstanceFor[ConnectorParser](className, Nil) match {
        case Success(parse) => registerConnectorParser(parse)
        case Failure(e)     => logger.error(s"未知的ConnectorParse", e)
      }
    }
    FusionCore(system).shutdowns.serviceUnbind("ConnectorSystem") { () =>
      Future {
        connectors.foreach { case (_, c) => c.close() }
        Done
      }(system.executionContext)
    }
  }

  def name: String = system.name

  def getConnector(name: String): Option[Connector] = _connectors.get(name)

  def connectors: Map[String, Connector] = _connectors

  def registerConnector(c: Connector): Map[String, Connector] = {
    _connectors = _connectors.updated(c.name, c)
    _connectors
  }

  def parsers: Map[String, ConnectorParser] = _parsers

  def registerConnectorParser(parse: ConnectorParser): Map[String, ConnectorParser] = {
    _parsers = _parsers.updated(parse.`type`, parse)
    logger.info(s"注册Connector解析器：$parse，当前数量：${parsers.size}")
    parsers
  }

  def fromFile(path: Path): Option[Connector] = ???

  def fromXML(node: scala.xml.Node): Option[Connector] = {
    import mass.core.XmlUtils.XmlRich
    val maybeParser = parsers.get(node.attr("type"))
    maybeParser.map(cp => cp.parseFromXML(node))
  }
}

object ConnectorSystem extends FusionExtensionId[ConnectorSystem] {
  override def createExtension(system: ActorSystem[_]): ConnectorSystem = new ConnectorSystem(system)
}
