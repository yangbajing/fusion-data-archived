package mass.scheduler.web.route

import akka.http.scaladsl.server.Route
import mass.http.AbstractRoute
import mass.scheduler.SchedulerSystem

class Routes(schedulerSystem: SchedulerSystem) extends AbstractRoute {

  def route: Route =
    new JobRoute(schedulerSystem).route

}
