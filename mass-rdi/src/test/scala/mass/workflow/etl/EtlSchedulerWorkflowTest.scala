package mass.workflow.etl

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import mass.job.JobScheduler
import mass.rdp.RdpSystem
import org.scalatest.wordspec.AnyWordSpecLike

class EtlSchedulerWorkflowTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  var rdpSystem: RdpSystem = _
  var jobSystem: JobScheduler = _
  //  var etlWorkflow: EtlWorkflow = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    rdpSystem = RdpSystem(system)
    jobSystem = JobScheduler(system)
    //    etlWorkflow = EtlWorkflow.fromXML(TestStub.graphXmlConfig, rdpSystem).get
  }

  override protected def afterAll(): Unit =
    //    etlWorkflow.close()
    super.afterAll()

  "EtlSchedulerWorkflowTest" should {
    "scheduler" in {
//      val conf = JobConf
//        .builder("test", "test")
//        .withCronExpress("10 * * * * ?")
//        .result
//      jobSystem.schedulerJob(conf, classOf[EtlJob], Map(EtlJob.WORKFLOW_STRING -> TestStub.graphConfig))
//
//      TimeUnit.MINUTES.sleep(5)
    }
  }
}
