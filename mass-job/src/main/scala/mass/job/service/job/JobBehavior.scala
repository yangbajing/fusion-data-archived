package mass.job.service.job

import akka.actor.typed.scaladsl.{ ActorContext, Behaviors }
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import akka.cluster.typed.{ ClusterSingleton, ClusterSingletonSettings, SingletonActor }
import fusion.json.CborSerializable
import mass.core.Constants
import mass.job.JobSystem
import mass.job.service.job.JobBehavior.CommandReply
import mass.message.job._

import scala.concurrent.Future

object JobBehavior {
  sealed trait Command extends CborSerializable
  final case class CommandReply(message: JobMessage, replyTo: ActorRef[JobResponse]) extends Command
  final case class CommandEvent(event: JobEvent) extends Command

  val NAME = "job"

  def init(system: ActorSystem[_]): ActorRef[Command] = {
    ClusterSingleton(system).init(
      SingletonActor(apply(), NAME).withSettings(ClusterSingletonSettings(system).withRole(Constants.Roles.CONSOLE)))
  }

  private def apply(): Behavior[Command] = Behaviors.setup[Command](context => new JobBehavior(context).init())
}

import mass.job.service.job.JobBehavior._
class JobBehavior(context: ActorContext[Command]) extends JobService {
  import context.executionContext
  override val jobSystem: JobSystem = JobSystem(context.system)

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

  def receiveMessage(message: JobMessage): Future[JobResponse] = message match {
    case req: JobTriggerReq      => handleScheduleJob(req)
    case req: JobPageReq         => handlePage(req)
    case req: JobFindReq         => handleFind(req)
    case req: JobUploadJobReq    => handleUploadJob(req)
    case req: JobListReq         => handleList(req)
    case req: JobGetAllOptionReq => handleGetAllOption(req)
    case req: JobCreateReq       => handleCreateJob(req)
    case req: JobUpdateReq       => handleUpdate(req)
    case req: JobUploadFilesReq  => handleUploadFiles(req)
  }

  def receiveEvent(v: JobEvent): Unit = v match {
    case event: JobTriggerEvent => triggerJob(event)
  }
}
