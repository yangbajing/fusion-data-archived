package mass.workflow.etl

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helloscala.common.test.HelloscalaSpec
import mass.job.JobSystem
import mass.rdp.RdpSystem
import org.scalatest.BeforeAndAfterAll

class EtlSchedulerWorkflowTest extends TestKit(ActorSystem("etl-test")) with HelloscalaSpec with BeforeAndAfterAll {
  var rdpSystem: RdpSystem = _
  var jobSystem: JobSystem = _
  //  var etlWorkflow: EtlWorkflow = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    rdpSystem = RdpSystem(system)
    jobSystem = JobSystem(system)
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
