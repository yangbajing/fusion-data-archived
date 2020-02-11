package mass.workflow.etl

import scala.xml.XML

object TestStub {
  lazy val graphConfig = {
    val s = scala.io.Source
      .fromInputStream(getClass.getClassLoader.getResourceAsStream("mass/workflow/etl/EtlWorkflowTest.xml"))
    try {
      s.getLines().mkString
    } finally s.close()
  }

  lazy val graphXmlConfig = XML.loadString(graphConfig)
}
