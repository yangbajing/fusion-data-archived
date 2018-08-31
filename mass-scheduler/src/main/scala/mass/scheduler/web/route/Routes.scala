package mass.scheduler.web.route

import akka.http.scaladsl.server.Route
import mass.http.AbstractRoute
import mass.scheduler.SchedulerSystem
import mass.scheduler.boot.Services
import mass.scheduler.web.route.api.ApiRoute

class Routes(schedulerSystem: SchedulerSystem, services: Services) extends AbstractRoute {

  def route: Route =
    new ApiRoute(schedulerSystem, services).route

}
