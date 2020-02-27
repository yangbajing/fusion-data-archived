package mass.job.service.job

import fusion.inject.guice.testkit.GuiceApplicationTestkit
import javax.inject.{ Inject, Singleton }
import mass.core.ProgramVersion
import mass.job.JobScheduler
import mass.message.job._
import mass.model.job.{ JobItem, JobTrigger, Program, TriggerType }
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration._

@Singleton
class JobServiceMock @Inject() (val jobScheduler: JobScheduler) extends JobServiceComponent

class JobServiceTest extends GuiceApplicationTestkit with AnyWordSpecLike {
  private implicit val ec = typedSystem.executionContext
  private val jobService = injectInstance[JobServiceMock]

  "JobServiceTest" should {
    val item = JobItem(
      Program.SCALA,
      Nil,
      programMain = "test.Main",
      programVersion = ProgramVersion.Scala212.version,
      description = Some("测试描述"))
    val trigger = JobTrigger(TriggerType.SIMPLE, repeat = 5, interval = 10.seconds)

//    "handleJobPage be empty" in {
//      val resp = jobService.handlePage(JobPageReq()).futureValue
//      resp.content should be(empty)
//    }

    "handleCreateJob" in {
      try {
        val req = JobCreateReq(Some("测试"), item, trigger)
        val resp = jobService.handleCreateJob(req).futureValue
        println(s"resp is $resp")
        val schedule = resp.schedule.value
        println(schedule)
      } catch {
        case e: Throwable =>
          e.printStackTrace()
      }
    }

//    "handleJobPage not be empty" in {
//      val req = JobPageReq(size = 30)
//      val resp = jobService.handlePage(req).futureValue
//      println(Jackson.prettyStringify(resp))
//      resp.totalElements should be > 0L
//      resp.content should not be empty
//    }

//    "handleUpdate" in {
//      val req = JobUpdateReq("测试", data = Some(Map("test" -> "test")), timeout = Some(60.minutes))
//      val resp = jobService.handleUpdate(req).futureValue
//      val schedule = resp.schedule.value
//      val jobItem = schedule.toJobItem
//      val jobTrigger = schedule.toJobTrigger
//      jobItem.data should contain key "test"
//      jobTrigger.timeout shouldBe 60.minutes
//    }
//
//    "handleUploadJob" in {
//      pending
//    }
//
//    "handleGetItem" in {
//      val req = JobFindReq("测试")
//      val resp = jobService.handleFind(req).futureValue
//      println(Jackson.prettyStringify(resp))
//      resp.schedule should not be empty
//    }

//    "handleGetAllOption" in {
//      val resp = jobService.handleGetAllOption(JobGetAllOptionReq())
//      resp.jobStatus should not be empty
//      resp.program should not be empty
//      resp.programVersion should not be empty
//      resp.triggerType should not be empty
//    }

//    "handleExecutionJob" in {
//      val event = JobTriggerEvent("测试")
//      jobService.triggerJob(event)
//      TimeUnit.SECONDS.sleep(30)
//    }
  }
}
