package mass.connector.sql

import java.util.Properties

import helloscala.common.Configuration
import mass.connector.{ConnectorParser, ConnectorSetting}
import mass.core.XmlUtils

import scala.xml.Node

class SQLConnectorParser extends ConnectorParser {

  import mass.core.XmlUtils.XmlRich

  override val `type` = "jdbc"

  def parseSettingFromXML(node: Node): ConnectorSetting = {
    val props = new Properties()

    val id = node.attr("name")
    props.put("poolName", id)
    (node \\ "props" \\ "prop").foreach { prop =>
      val key = (prop \\ "@key").text
      val value = getText(prop)
      props.put(key, value)
    }
    ConnectorSetting(Configuration(props))
  }

  override def parseFromXML(node: Node): SQLConnector = {
    val setting = parseSettingFromXML(node)
    SQLConnector(node.attr("name"), setting)
  }

  @inline private def getText(prop: Node): String =
    prop.getAttr("value").getOrElse(XmlUtils.text(prop \ "value"))

}
