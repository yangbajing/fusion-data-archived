package mass.workflow.etl

import fusion.inject.guice.testkit.GuiceApplicationTestkit
import mass.job.JobScheduler
import mass.rdp.RdpSystem
import org.scalatest.wordspec.AnyWordSpecLike

class EtlSchedulerWorkflowTest extends GuiceApplicationTestkit with AnyWordSpecLike {
  private val rdpSystem: RdpSystem = injectInstance[RdpSystem]
  private val jobSystem: JobScheduler = injectInstance[JobScheduler]

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
