package mass.scheduler.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import helloscala.common.Configuration
import mass.http.{AbstractRoute, HSAkkaHttpServer}
import mass.scheduler.SchedulerSystem
import mass.scheduler.web.route.Routes

import scala.concurrent.Future

class WebServer(schedulerSystem: SchedulerSystem) extends HSAkkaHttpServer {

  override def actorSystem: ActorSystem = schedulerSystem.massSystem.system

  override def actorMaterializer: ActorMaterializer = ActorMaterializer()(schedulerSystem.massSystem.system)

  override val hlServerValue: String = "mass-scheduler"

  override def configuration: Configuration = schedulerSystem.massSystem.configuration

  override def routes: AbstractRoute = new Routes(schedulerSystem)

  override def close(): Unit = {}

  /**
   * 启动基于Akka HTTP的服务
   * @return
   */
  def startServer(): (Future[Http.ServerBinding], Option[Future[Http.ServerBinding]]) = startServer(
    configuration.getString("mass.scheduler.server.host"),
    configuration.getInt("mass.scheduler.server.port"),
    configuration.get[Option[Int]]("mass.scheduler.server.https-port"))

}
