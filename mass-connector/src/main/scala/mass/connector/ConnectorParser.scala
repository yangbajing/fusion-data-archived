package mass.connector

trait ConnectorParser {
  def `type`: String

  def parseFromXML(node: scala.xml.Node): Connector

  override def toString = s"SQLConnectorParser(${`type`})"
}

