package mass.rdp.etl.graph

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.util.StringUtils
import mass.core.XmlUtils

import scala.util.Try
import scala.xml.{NodeSeq, XML}

trait EtlGraphParser {

  def parse(): Try[EtlGraphSetting]

  def validation(setting: EtlGraphSetting): Try[EtlGraphSetting] = Try {
    val sourceOut = setting.source.out
    val sinkName = setting.sink.name
    if (!(setting.flows.exists(_.name == sourceOut) || sinkName == sourceOut)) {
      throw new EtlGraphException("source.out未找到指定的flow或sink")
    }

    if (!(setting.flows
          .exists(_.outs.exists(_ == sinkName)) || sourceOut == sinkName)) {
      throw new EtlGraphException("graph不是闭合的")
    }

    // TODO 其它 graph 校验

    setting
  }

}

trait EtlGraphParserFactory {
  def `type`: String
}

class EtlGraphXmlParserFactory extends EtlGraphParserFactory {

  override def `type`: String = "xml"

  def build(elem: NodeSeq): EtlGraphParser = new EtlGraphXmlParser(elem)

  class EtlGraphXmlParser(elem: NodeSeq) extends EtlGraphParser with StrictLogging {

    import mass.core.XmlUtils.XmlRich

    logger.trace(s"parse elem:\n$elem")

    def parse(): Try[EtlGraphSetting] = {
      val name = elem.attr("name")
      require(StringUtils.isNoneBlank(name), s"graph需要设置id属性：$elem")

      val source = parseSource(elem \ "source")
      val flows = (elem \ "flows" \ "flow").map(parseFlow).toVector
      val sink = parseSink(elem \ "sink")

      validation(EtlGraphSetting(name, source, flows, sink))
    }

    private def parseSource(node: NodeSeq): EtlSource = {
      val name = node.attr("name")
      val connector = parseConnector(node \ "connector")
      val script = parseScript(node \ "script")
      val out = XmlUtils.text(node \ "out")
      EtlSource(name, connector, script, out)
    }

    private def parseFlow(node: NodeSeq): EtlFlow = {
      val name = node.attr("name")
      val script = parseScript(node \ "script")
      val outs = (node \ "out").map(XmlUtils.text).toVector
      EtlFlow(name, script, outs)
    }

    private def parseSink(node: NodeSeq): EtlSink = {
      val name = node.attr("name")
      val connector = parseConnector(node \ "connector")
      val script = parseScript(node \ "script")
      EtlSink(name, connector, script)
    }

    @inline private def parseScript(node: NodeSeq): EtlScript = {
      logger.trace(s"parse script:\n$node")
      EtlScript(EtlScriptType.withName(node.attr("type")), node.getAttr("src"), node.getText)
    }

    @inline private def parseConnector(node: NodeSeq): EtlConnector =
      EtlConnector(node.attr("ref"))

  }

}
