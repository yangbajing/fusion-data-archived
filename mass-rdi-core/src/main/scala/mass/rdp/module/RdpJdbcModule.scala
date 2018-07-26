package mass.rdp.module

import mass.connector.ConnectorType
import mass.rdp.etl.graph.{
  EtlGraphParserFactory,
  EtlGraphXmlParserFactory,
  EtlStreamFactory,
  EtlStreamJdbcFactory
}

class RdpJdbcModule extends RdpModule {
  override val name: String = "jdbc"

  override def `type`: String = ConnectorType.JDBC.toString

  override def etlStreamBuilders: Vector[EtlStreamFactory] = Vector(
    new EtlStreamJdbcFactory()
  )

  override def graphParserFactories: Vector[EtlGraphParserFactory] = Vector(
    new EtlGraphXmlParserFactory()
  )
}
