package mass.rdp.etl

import mass.core.job.{JobResult, SchedulerContext, SchedulerJob}
import mass.rdp.RdpSystem
import mass.rdp.etl.graph.EtlGraphException

import scala.concurrent.Future
import scala.xml.XML

class EtlJob extends SchedulerJob {
  import EtlJob._

  override def run(context: SchedulerContext): Future[JobResult] = {
    val rdpSystem = RdpSystem(context.system)
    val xmlString = context.data
      .getOrElse(WORKFLOW_STRING, throw new EtlGraphException(s"流程配置未设置，SchedulerJob.data.key = $WORKFLOW_STRING"))
    val workflow = EtlWorkflow.fromXML(XML.loadString(xmlString), rdpSystem).get
    workflow
      .run()
      .future
      .map { result =>
        EtlJobResult(result)
      }(rdpSystem.system.dispatcher)
  }

}

object EtlJob {
  val WORKFLOW_STRING = "WORKFLOW_STRING"
}
