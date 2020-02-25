package mass.job.service.job

import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.cluster.typed.{ ClusterSingleton, ClusterSingletonSettings, SingletonActor }
import fusion.json.CborSerializable
import helloscala.common.IntStatus
import mass.core.Constants
import mass.job.JobScheduler
import mass.job.service.job.JobActor.CommandReply
import mass.message.job._

import scala.concurrent.Future

object JobActor {
  sealed trait Command extends CborSerializable
  final case class CommandReply(message: JobMessage, replyTo: ActorRef[JobResponse]) extends Command
  final case class CommandEvent(event: JobEvent) extends Command

  val NAME = "job"

  def init(system: ActorSystem[_]): ActorRef[Command] = {
    ClusterSingleton(system).init(
      SingletonActor(apply(), NAME).withSettings(ClusterSingletonSettings(system).withRole(Constants.Roles.CONSOLE)))
  }

  private def apply(): Behavior[Command] = Behaviors.setup[Command](context => new JobActor(context).init())
}

import mass.job.service.job.JobActor._
class JobActor private (context: ActorContext[Command]) extends JobServiceComponent {
  import context.executionContext
  override val jobScheduler: JobScheduler = JobScheduler(context.system)

  def init(): Behavior[Command] = {
    receive()
  }

  def receive(): Behavior[Command] = Behaviors.receiveMessage[Command] {
    case CommandReply(message, replyTo) =>
      receiveMessage(message).foreach(resp => replyTo ! resp)
      Behaviors.same
    case CommandEvent(event) =>
      receiveEvent(event)
      Behaviors.same
  }

  private def receiveMessage(message: JobMessage): Future[JobResponse] =
    try {
      val future = message match {
        case req: JobScheduleReq     => handleScheduleJob(req)
        case req: JobPageReq         => handlePage(req)
        case req: JobFindReq         => handleFind(req)
        case req: JobUploadJobReq    => handleUploadJob(req)
        case req: JobListReq         => handleList(req)
        case req: JobGetAllOptionReq => Future(handleGetAllOption(req))
        case req: JobCreateReq       => handleCreateJob(req)
        case req: JobUpdateReq       => handleUpdate(req)
        case req: JobUploadFilesReq  => handleUploadFiles(req)
      }
      future.recover {
        case e =>
          val message = s"Handle message error: ${e.getMessage}."
          logger.error(message, e)
          JobErrorResponse(IntStatus.INTERNAL_ERROR, message)
      }
    } catch {
      case e: Throwable =>
        val message = s"Process message error: ${e.getMessage}."
        logger.error(message)
        Future.successful(JobErrorResponse(IntStatus.INTERNAL_ERROR, message))
    }

  private def receiveEvent(v: JobEvent): Unit =
    try {
      v match {
        case event: JobTriggerEvent => triggerJob(event)
      }
    } catch {
      case e: Throwable => logger.error(s"Process event error: ${e.getMessage}", e)
    }
}
