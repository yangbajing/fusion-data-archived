package mass.rdp.etl

import mass.rdp.RdpSystem
import mass.rdp.etl.graph.EtlGraphException
import mass.core.job.{JobResult, SchedulerContext, SchedulerJob}

import scala.concurrent.Future
import scala.xml.XML

class EtlJob extends SchedulerJob {
  import EtlJob._

  override def run(context: SchedulerContext): Future[JobResult] = {
    val xmlString = context.data.getOrElse(WORKFLOW_STRING, throw new EtlGraphException(s"流程配置未设置，SchedulerJob.data.key = $WORKFLOW_STRING"))
    val workflow = EtlWorkflow.fromXML(XML.loadString(xmlString), RdpSystem.instance).get
    workflow.run().future.map { result =>
      EtlJobResult(result)
    }(context.scheduler.dispatcher)
  }

}

object EtlJob {
  val WORKFLOW_STRING = "WORKFLOW_STRING"
}
