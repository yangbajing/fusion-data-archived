package mass.scheduler.web.route

import akka.http.scaladsl.server.Route
import mass.http.AbstractRoute
import mass.scheduler.SchedulerSystem
import mass.server.MassSystemExtension

class Routes(schedulerSystem: SchedulerSystem) extends AbstractRoute {

  def route: Route =
    new JobRoute(schedulerSystem).route

}
