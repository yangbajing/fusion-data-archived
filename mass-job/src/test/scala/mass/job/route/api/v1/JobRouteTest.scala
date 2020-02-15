package mass.job.route.api.v1

import akka.http.scaladsl.model.StatusCodes
import mass.job.SchedulerSpec
import mass.message.job.JobPageResp

class JobRouteTest extends SchedulerSpec {
  private lazy val route = new JobRoute()(typedSystem).route

  "JobRoute" should {
    "page" in {
      import fusion.json.jackson.http.JacksonSupport._
      Get("/job/page") ~> route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[JobPageResp]
        println(resp)
        resp should not be null
      }
    }
  }
}
