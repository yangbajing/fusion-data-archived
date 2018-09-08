package mass.rdp.module

import mass.core.module.Module
import mass.rdp.etl.graph.{EtlGraphParserFactory, EtlStreamFactory}

trait RdpModule extends Module {

  def `type`: String

  def etlStreamBuilders: Vector[EtlStreamFactory]

  def graphParserFactories: Vector[EtlGraphParserFactory]

}
