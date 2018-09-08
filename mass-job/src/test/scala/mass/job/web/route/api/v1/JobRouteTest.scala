package mass.job.web.route.api.v1

import akka.http.scaladsl.model.StatusCodes
import helloscala.common.jackson.Jackson
import mass.job.SchedulerSpec
import mass.job.service.Services
import mass.job.service.job.JobActor
import mass.message.job.{JobCreateReq, JobPageResp}

class JobRouteTest extends SchedulerSpec {

  private lazy val services = new Services(jobSystem, List(JobActor.props))
  private lazy val route = new JobRoute(services).route

  "JobRoute" should {
    "page" in {
      import mass.http.JacksonSupport._
      Get("/job/page") ~> route ~> check {
        status mustBe StatusCodes.OK
        val resp = responseAs[JobPageResp]
        println(resp)
        resp must not be null
      }
    }

  }

  val jsonCreateJob =
    """{"item":{"programVersion":"2.12","key":"ddd","program":1,"programOptions":[],"programMain":"test.Main","programArgs":[]},"trigger":{"triggerType":2,"key":"ddd","startTime":null,"endTime":null,"duration":"1.day"}}"""

  "mock" should {
    "createJob" in {
      val jsonNode = Jackson.readTree(jsonCreateJob)
      println(jsonNode)
      val req = Jackson.treeToValue[JobCreateReq](jsonNode)
      println(req)
      println(Jackson.prettyStringify(req))
    }

    "jackson" in {
      import scala.compat.java8.DurationConverters._
      val d = java.time.Duration.ofDays(3)
      println(Jackson.valueToTree(d))
      println(Jackson.stringify(d))

      val node = Jackson.readTree("69")
      val jd = Jackson.treeToValue[java.time.Duration](node)
      println(jd)
      println(jd.toScala)
    }
  }

}
