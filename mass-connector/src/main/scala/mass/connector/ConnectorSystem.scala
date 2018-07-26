package mass.connector

import java.nio.file.Path

import akka.actor.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import mass.core.{BaseSystem, MassSystem}

object ConnectorSystem {

  def apply(name: String, massSystem: MassSystem): ConnectorSystem =
    new ConnectorSystem(name, massSystem)

}

class ConnectorSystem private (val name: String, val massSystem: MassSystem)
    extends BaseSystem
    with StrictLogging {
  private var _parsers = Map.empty[String, ConnectorParser]
  private var _connectors = Map.empty[String, Connector]
  init()

  private def init(): Unit = {
    massSystem.configuration
      .get[Seq[String]]("mass.connector.parsers")
      .foreach { className =>
        Class.forName(className).newInstance() match {
          case parse: ConnectorParser => registerConnectorParser(parse)
          case unknown                => logger.error(s"未知的ConnectorParse: $unknown")
        }
      }
    massSystem.system.registerOnTermination {
      connectors.foreach { case (_, c) => c.close() }
    }
  }

  override def system: ActorSystem = massSystem.system

  override def configuration: Configuration = massSystem.configuration

  def getConnector(name: String): Option[Connector] = _connectors.get(name)

  def connectors: Map[String, Connector] = _connectors

  def registerConnector(c: Connector): Map[String, Connector] = {
    _connectors = _connectors.updated(c.name, c)
    _connectors
  }

  def parsers: Map[String, ConnectorParser] = _parsers

  def registerConnectorParser(
      parse: ConnectorParser): Map[String, ConnectorParser] = {
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
