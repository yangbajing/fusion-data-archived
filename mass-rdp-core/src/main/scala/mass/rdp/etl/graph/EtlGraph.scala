package mass.rdp.etl.graph

import helloscala.common.util.StringUtils
import mass.connector.Connector
import mass.rdp.RdpSystem
import mass.rdp.etl.EtlWorkflowExecution
import mass.rdp.etl.graph.EtlScriptType.EtlScriptType

import scala.collection.immutable

case class EtlConnector(ref: String) {
  require(StringUtils.isNoneBlank(ref), s"ref: $ref 需要指定数据连接器")
}

object EtlScriptType extends Enumeration {
  type EtlScriptType = Value

  val sql = Value(1)
  val scala = Value(2)
  val javascript = Value(3)
  val java = Value(4)
}

case class EtlScript(
    `type`: EtlScriptType,
    src: Option[String],
    content: Option[String]) {
  require(src.nonEmpty || content.nonEmpty, s"src: ${src}属性 或 script内容${content}不能同时为空")
}

case class EtlSource(
    name: String,
    connector: EtlConnector,
    script: EtlScript,
    out: String) {
  require(StringUtils.isNoneBlank(name), "name 不能为空")
  require(StringUtils.isNoneBlank(out), "out 不能为空")
}

case class EtlFlow(
    name: String,
    script: EtlScript,
    outs: Vector[String]) {
  require(StringUtils.isNoneBlank(name), "name 不能为空")
  require(outs.nonEmpty, "outs 不能为空")
}

case class EtlSink(
    name: String,
    connector: EtlConnector,
    script: EtlScript) {
  require(StringUtils.isNoneBlank(name), "name 不能为空")
}

case class EtlGraphSetting(name: String, source: EtlSource, flows: Vector[EtlFlow], sink: EtlSink)

trait EtlGraph {
  def name: String = graphSetting.name

  def graphSource: EtlSource = graphSetting.source

  def graphFlows: Vector[EtlFlow] = graphSetting.flows

  def graphSink: EtlSink = graphSetting.sink

  def graphSetting: EtlGraphSetting

  def run(connectors: immutable.Seq[Connector], rdpSystem: RdpSystem): EtlWorkflowExecution
}
