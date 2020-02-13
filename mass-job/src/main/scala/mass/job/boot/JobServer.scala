package mass.job.boot

import akka.http.scaladsl.Http
import fusion.http.FusionHttpServer
import fusion.http.server.AbstractRoute
import mass.job.JobSystem
import mass.job.service.Services
import mass.job.web.route.Routes

class JobServer(jobSystem: JobSystem) {
  val services = new Services(jobSystem)

  /**
   * 启动基于Akka HTTP的服务
   * @return
   */
  def startServer(): Http.ServerBinding = {
    val routes: AbstractRoute = new Routes(services)
    FusionHttpServer(jobSystem.system).component.startAbstractRouteSync(routes)
  }
}
