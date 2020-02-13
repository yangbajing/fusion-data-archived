package mass.job.web.route

import akka.http.scaladsl.server.Route
import fusion.http.server.AbstractRoute
import mass.job.service.Services
import mass.job.web.route.api.{ ApiRoute, MockRoute }

class Routes(services: Services) extends AbstractRoute {
  def route: Route =
    pathPrefix("job") {
      new ApiRoute(services).route
    } ~ new MockRoute().route
}
