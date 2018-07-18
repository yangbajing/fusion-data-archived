package mass.workflow.etl

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helloscala.common.test.HelloscalaSpec
import mass.connector.ConnectorSystem
import mass.core.MassSystem
import mass.core.job.JobConf
import mass.rdp.RdpSystem
import mass.rdp.etl.EtlJob
import mass.scheduler.SchedulerSystem
import mass.server.MassSystemExtension
import org.scalatest.BeforeAndAfterAll

class EtlSchedulerWorkflowTest extends TestKit(ActorSystem("etl-test")) with HelloscalaSpec with BeforeAndAfterAll {
  var rdpSystem: RdpSystem = _
  var schedulerSystem: SchedulerSystem = _
  //  var etlWorkflow: EtlWorkflow = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    val massSystem = MassSystem("mass", system).as[MassSystemExtension]
    rdpSystem = RdpSystem("rdp-test", massSystem, ConnectorSystem(massSystem))
    schedulerSystem = SchedulerSystem(massSystem)
    //    etlWorkflow = EtlWorkflow.fromXML(TestStub.graphXmlConfig, rdpSystem).get
  }

  override protected def afterAll(): Unit = {
    //    etlWorkflow.close()
    super.afterAll()
  }

  "EtlSchedulerWorkflowTest" should {

    "scheduler" in {
      val conf = JobConf.builder("test", "test")
        .withCronExpress("10 * * * * ?")
        .result
      schedulerSystem.schedulerJob(conf, classOf[EtlJob], Map(EtlJob.WORKFLOW_STRING -> TestStub.graphConfig))

      TimeUnit.MINUTES.sleep(5)
    }

  }

}
