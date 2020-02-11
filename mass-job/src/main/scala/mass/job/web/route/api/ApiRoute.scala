package mass.job.web.route.api

import akka.http.scaladsl.server.Route
import mass.http.AbstractRoute
import mass.job.service.Services
import mass.job.web.route.api.v1.JobRoute

class ApiRoute(services: Services) extends AbstractRoute {
  override def route: Route = pathPrefix("api") {
    pathPrefix("v1") {
      new JobRoute(services).route
    }
  }
}
