package mass.job.route

import akka.http.scaladsl.server.Route
import fusion.http.server.BaseRoute
import javax.inject.Inject
import mass.job.route.api.{ ApiRoute, MockRoute }

class Routes @Inject() (apiRoute: ApiRoute, mockRoute: MockRoute) extends BaseRoute {
  def route: Route =
    pathPrefix("job") {
      apiRoute.route
    } ~ mockRoute.route
}
