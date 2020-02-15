package mass.job.route.api

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Route
import fusion.common.FusionProtocol
import fusion.http.server.AbstractRoute
import mass.job.route.api.v1.JobRoute

class ApiRoute()(implicit system: ActorSystem[FusionProtocol.Command]) extends AbstractRoute {
  override def route: Route = pathPrefix("api") {
    pathPrefix("v1") {
      new JobRoute().route
    }
  }
}
