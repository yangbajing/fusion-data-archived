package mass.rdp.extension

import mass.connector.ConnectorType
import mass.rdp.etl.graph.{EtlGraphParserFactory, EtlGraphXmlParserFactory, EtlStreamFactory, EtlStreamJdbcFactory}

class RdpJdbcExtension extends RdpExtension {
  override def `type`: String = ConnectorType.JDBC.toString

  override def etlStreamBuilders: Vector[EtlStreamFactory] = Vector(
    new EtlStreamJdbcFactory()
  )

  override def graphParserFactories: Vector[EtlGraphParserFactory] = Vector(
    new EtlGraphXmlParserFactory()
  )

}
