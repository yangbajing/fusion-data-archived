package mass.core

import helloscala.common.util.StringUtils

import scala.xml.NodeSeq

object XmlUtils {

  implicit class XmlRich(node: NodeSeq) {

    @inline def attr(attr: String): String = (node \ s"@$attr").text.trim

    @inline def getAttr(attr: String): Option[String] = StringUtils.option(this.attr(attr)).map(_.trim)

    @inline def text: String = node.text.trim

    @inline def getText: Option[String] = StringUtils.option(node.text).map(_.trim)

  }

  def attr(ns: NodeSeq, attr: String): String = ns.attr(attr)

  def getAttr(ns: NodeSeq, attr: String): Option[String] = ns.getAttr(attr)

  def text(ns: NodeSeq): String = ns.text

  def getText(ns: NodeSeq): Option[String] = ns.getText

}
