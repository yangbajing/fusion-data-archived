package mass.rdp.extension

import mass.core.extension.Extension
import mass.rdp.etl.graph.{EtlGraphParserFactory, EtlStreamFactory}

trait RdpExtension extends Extension {

  def `type`: String

  def etlStreamBuilders: Vector[EtlStreamFactory]

  def graphParserFactories: Vector[EtlGraphParserFactory]

}
