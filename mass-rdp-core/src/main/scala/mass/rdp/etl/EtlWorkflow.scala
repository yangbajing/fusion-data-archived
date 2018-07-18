package mass.rdp.etl

import java.nio.file.Path

import mass.connector.Connector
import mass.rdp.RdpSystem
import mass.rdp.etl.graph.{EtlGraph, EtlGraphException, EtlGraphImpl, EtlGraphXmlParserFactory}
import mass.core.workflow.Workflow

import scala.collection.immutable
import scala.util.{Failure, Try}
import scala.xml.{Elem, XML}

/**
 * ETL工作流定义
 * @param connectors workflow范围数据源连接器
 */
case class EtlWorkflow(connectors: immutable.Seq[Connector], graph: EtlGraph, rdpSystem: RdpSystem) extends Workflow[EtlResult] with AutoCloseable {

  override def close(): Unit = {
    connectors.foreach(_.close())
  }

  override def run(): EtlWorkflowExecution = {
    graph.run(connectors, rdpSystem)
  }

}

object EtlWorkflow {

  def fromFile(path: Path, rdpSystem: RdpSystem): Try[EtlWorkflow] = fromXML(XML.loadFile(path.toFile), rdpSystem)

  def fromString(workflow: String, rdpSystem: RdpSystem): Try[EtlWorkflow] = fromXML(XML.loadString(workflow), rdpSystem)

  def fromXML(workflow: Elem, rdpSystem: RdpSystem): Try[EtlWorkflow] = {
    require(workflow.head.label == "workflow", s"workflow必需为根元素。elem: $workflow")

    val connectors = (workflow \ "connectors" \ "connector").flatMap(node => rdpSystem.connectorSystem.fromXML(node))
    rdpSystem.graphParserFactories.get("xml") match {
      case Some(factory) =>
        factory.asInstanceOf[EtlGraphXmlParserFactory]
          .build((workflow \ "graph").head)
          .parse()
          .map(setting => new EtlWorkflow(connectors, EtlGraphImpl(setting), rdpSystem))
      case _ => Failure(new EtlGraphException("EtlGraphParserFactory type: xml 不存在"))
    }
  }

}
