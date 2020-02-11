package mass.job.service.job

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helloscala.common.jackson.Jackson
import helloscala.common.test.HelloscalaSpec
import mass.Global
import mass.job.JobSystem
import mass.job.util.ProgramVersion
import mass.message.job._
import mass.data.job.{ JobItem, JobTrigger, Program, TriggerType }

import scala.concurrent.duration._

class JobServiceMock(val jobSystem: JobSystem) extends JobService

class JobServiceTest extends TestKit(ActorSystem("job-service-test")) with HelloscalaSpec {
  Global.registerActorSystem(system)

  import system.dispatcher
  val jobSystem = JobSystem(system)
  val jobService = new JobServiceMock(jobSystem)

  "JobServiceTest" should {
    val item = JobItem(
      Program.SCALA,
      programMain = "test.Main",
      programVersion = ProgramVersion.Scala212.VERSION,
      description = "测试描述")
    val trigger = JobTrigger(TriggerType.SIMPLE, repeat = 5, duration = 10.seconds)

    "handleJobPage be empty" in {
      val resp = jobService.handlePage(JobPageReq()).futureValue
      resp.content must be(empty)
    }

    "handleCreateJob" in {
      val req = JobCreateReq(Some("测试"), Some(item), Some(trigger), item.description)
      val resp = jobService.handleCreateJob(req).futureValue
      val schedule = resp.schedule.value
      schedule.item must not be empty
      schedule.trigger must not be empty
      println(resp)
    }

    "handleJobPage not be empty" in {
      val req = JobPageReq(page = 1, size = 30)
      val resp = jobService.handlePage(req).futureValue
      println(Jackson.prettyStringify(resp))
      resp.totalElements must be > 0L
      resp.content must not be empty
    }

    "handleUpdate" in {
      val req =
        JobUpdateReq("测试", Some(item.copy(data = Map("test" -> "test"))), Some(trigger.copy(timeout = 60.minutes)))
      val resp = jobService.handleUpdate(req).futureValue
      val schedule = resp.schedule.value
      val jobItem = schedule.item.value
      val jobTrigger = schedule.trigger.value
      jobItem.data must contain key "test"
      jobTrigger.timeout mustBe 60.minutes
    }

    "handleUploadJob" in {
      pending
    }

    "handleGetItem" in {
      val req = JobFindReq("测试")
      val resp = jobService.handleFind(req).futureValue
      println(Jackson.prettyStringify(resp))
      resp.schedule must not be empty
    }

    "handleGetAllOption" in {
      val resp = jobService.handleGetAllOption(JobGetAllOptionReq()).futureValue
      resp.jobStatus must not be empty
      resp.program must not be empty
      resp.programVersion must not be empty
      resp.triggerType must not be empty
    }

    "handleExecutionJob" in {
      val event = JobExecuteEvent("测试")
      jobService.executionJob(event)
      TimeUnit.SECONDS.sleep(30)
    }
  }
}
