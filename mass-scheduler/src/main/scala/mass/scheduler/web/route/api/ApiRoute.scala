package mass.scheduler.web.route.api
import akka.http.scaladsl.server.Route
import mass.http.AbstractRoute
import mass.scheduler.SchedulerSystem
import mass.scheduler.boot.Services
import mass.scheduler.web.route.api.v1.JobRoute

class ApiRoute(
    schedulerSystem: SchedulerSystem,
    services: Services
) extends AbstractRoute {

  override def route: Route = pathPrefix("api") {
    pathPrefix("v1") {
      new JobRoute(services).route
    }
  }

}
