package mass.job.route

import akka.http.scaladsl.server.Route
import fusion.http.server.AbstractRoute
import mass.Mass
import mass.job.route.api.{ ApiRoute, MockRoute }

class Routes(mass: Mass) extends AbstractRoute {
  private implicit val system = mass.system

  def route: Route =
    pathPrefix("job") {
      new ApiRoute().route
    } ~ new MockRoute().route
}
