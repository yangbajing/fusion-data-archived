package mass.scheduler.boot

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import helloscala.common.Configuration
import mass.http.{AbstractRoute, HSAkkaHttpServer}
import mass.scheduler.SchedulerSystem
import mass.scheduler.business.SchedulerMasterActor
import mass.scheduler.business.actors.JobActor
import mass.scheduler.web.route.Routes

import scala.concurrent.Future

class Services(
    val schedulerSystem: SchedulerSystem,
    propsList: Iterable[(Props, Symbol)]
) {
  implicit def ec = schedulerSystem.system.dispatcher

  val master: ActorRef =
    SchedulerMasterActor.props(schedulerSystem, propsList, SchedulerMasterActor.name.toString())(schedulerSystem.system)
}

class SchedulerServer(schedulerSystem: SchedulerSystem) extends HSAkkaHttpServer {

  override def actorSystem: ActorSystem = schedulerSystem.massSystem.system

  override def actorMaterializer: ActorMaterializer =
    ActorMaterializer()(schedulerSystem.massSystem.system)

  override val hlServerValue: String = "mass-scheduler"

  val services = new Services(schedulerSystem,
                              List(
                                JobActor.props(schedulerSystem)
                              ))

  override def configuration: Configuration =
    schedulerSystem.massSystem.configuration

  override def routes: AbstractRoute = new Routes(schedulerSystem, services)

  override def close(): Unit = {}

  /**
   * 启动基于Akka HTTP的服务
   * @return
   */
  def startServer(): (Future[Http.ServerBinding], Option[Future[Http.ServerBinding]]) =
    startServer(
      configuration.getString("mass.scheduler.server.host"),
      configuration.getInt("mass.scheduler.server.port"),
      configuration.get[Option[Int]]("mass.scheduler.server.https-port")
    )

}
