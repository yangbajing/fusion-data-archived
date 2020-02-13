package mass.job.service

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Path }

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import akka.http.scaladsl.server.directives.FileInfo
import akka.util.Timeout
import mass.job.JobSystem
import mass.job.service.job.JobBehavior
import mass.job.service.job.JobBehavior.CommandReply
import mass.message.job._

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

class Services(val jobSystem: JobSystem) {
  implicit val timeout: Timeout = Timeout(10.seconds)
  implicit val system = jobSystem.system

  val jobBehavior: ActorRef[JobBehavior.Command] = null

  def listOption(): Future[JobGetAllOptionResp] = {
    jobBehavior.ask[JobResponse](replyTo => CommandReply(JobGetAllOptionReq(), replyTo)).mapTo[JobGetAllOptionResp]
  }

  def uploadFiles(list: immutable.Seq[(FileInfo, File)])(implicit ec: ExecutionContext): Future[JobUploadFilesResp] = {
    jobBehavior
      .ask[JobResponse](replyTo => CommandReply(JobUploadFilesReq(list), replyTo))
      .mapTo[JobUploadFilesResp]
      .andThen {
        case _ => list.foreach { case (_, file) => Files.deleteIfExists(file.toPath) }
      }
  }

  def uploadJobOnZip(fileInfo: FileInfo, file: Path)(implicit ec: ExecutionContext): Future[JobUploadJobResp] = {
    val msg = JobUploadJobReq(
      file,
      fileInfo.fileName,
      fileInfo.contentType.charsetOption.map(_.nioCharset()).getOrElse(StandardCharsets.UTF_8))

    jobBehavior.ask[JobResponse](replyTo => CommandReply(msg, replyTo)).mapTo[JobUploadJobResp].andThen {
      case _ => Files.deleteIfExists(file)
    }
  }

  def updateTrigger(req: JobUpdateReq): Future[JobSchedulerResp] = {
    jobBehavior.ask[JobResponse](replyTo => CommandReply(req, replyTo)).mapTo[JobSchedulerResp]
  }

  def page(req: JobPageReq): Future[JobPageResp] = {
    jobBehavior.ask[JobResponse](replyTo => CommandReply(req, replyTo)).mapTo[JobPageResp]
  }

  def findItemByKey(key: String): Future[JobSchedulerResp] = {
    jobBehavior.ask[JobResponse](replyTo => CommandReply(JobFindReq(key = key), replyTo)).mapTo[JobSchedulerResp]
  }

  def createJob(req: JobCreateReq): Future[JobCreateResp] = {
    jobBehavior.ask[JobResponse](replyTo => CommandReply(req, replyTo)).mapTo[JobCreateResp]
  }

  def updateJob(req: JobUpdateReq): Future[JobSchedulerResp] = {
    jobBehavior.ask[JobResponse](replyTo => CommandReply(req, replyTo)).mapTo[JobSchedulerResp]
  }
}
