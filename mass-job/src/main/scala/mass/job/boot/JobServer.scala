package mass.job.boot

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import fusion.core.extension.FusionCore
import helloscala.common.Configuration
import mass.core.Constants
import mass.http.{ AbstractRoute, HSAkkaHttpServer }
import mass.job.JobSystem
import mass.job.service.Services
import mass.job.web.route.Routes

import scala.concurrent.Future

class JobServer(jobSystem: JobSystem) extends HSAkkaHttpServer {
  override def actorSystem: ActorSystem = FusionCore(jobSystem.system).classicSystem

  override val hlServerValue: String = "mass-scheduler"

  val services = new Services(jobSystem)

  override def configuration: Configuration = jobSystem.configuration

  override def routes: AbstractRoute = new Routes(services)

  /**
   * 启动基于Akka HTTP的服务
   * @return
   */
  def startServer(): (Future[Http.ServerBinding], Option[Future[Http.ServerBinding]]) =
    startServer(s"${Constants.BASE_CONF}.job.")
}
