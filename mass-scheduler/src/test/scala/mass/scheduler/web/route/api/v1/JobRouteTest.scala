package mass.scheduler.web.route.api.v1

import akka.http.scaladsl.model.StatusCodes
import mass.scheduler.SchedulerSpec
import mass.scheduler.boot.Services
import mass.scheduler.business.actors.JobActor
import mass.scheduler.model.JobPageResp

class JobRouteTest extends SchedulerSpec {

  private lazy val services = new Services(
    schedulerSystem,
    List(
      JobActor.props(schedulerSystem)
    )
  )
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

}
