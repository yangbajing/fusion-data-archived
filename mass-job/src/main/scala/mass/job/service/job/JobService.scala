package mass.job.service.job

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Path }

import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ ActorRef, ActorSystem }
import akka.http.scaladsl.server.directives.FileInfo
import akka.util.Timeout
import mass.job.service.job.JobActor.CommandReply
import mass.message.job._

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.reflect.ClassTag

class JobService(system: ActorSystem[_]) {
  implicit val st = system
  implicit val timeout: Timeout = Timeout(10.seconds)
  val jobActor: ActorRef[JobActor.Command] = JobActor.init(system)

  def listOption(): Future[JobGetAllOptionResp] = askToJob[JobGetAllOptionResp](JobGetAllOptionReq())

  def uploadFiles(list: immutable.Seq[(FileInfo, File)])(implicit ec: ExecutionContext): Future[JobUploadFilesResp] = {
    askToJob[JobUploadFilesResp](JobUploadFilesReq(list)).andThen {
      case _ => list.foreach { case (_, file) => Files.deleteIfExists(file.toPath) }
    }
  }

  def uploadJobOnZip(fileInfo: FileInfo, file: Path)(implicit ec: ExecutionContext): Future[JobUploadJobResp] = {
    val req = JobUploadJobReq(
      file,
      fileInfo.fileName,
      fileInfo.contentType.charsetOption.map(_.nioCharset()).getOrElse(StandardCharsets.UTF_8))
    askToJob[JobUploadJobResp](req).andThen { case _ => Files.deleteIfExists(file) }
  }

  def updateTrigger(req: JobUpdateReq): Future[JobSchedulerResp] = askToJob[JobSchedulerResp](req)

  def page(req: JobPageReq): Future[JobPageResp] = askToJob[JobPageResp](req)

  def findItemByKey(key: String): Future[JobSchedulerResp] = askToJob[JobSchedulerResp](JobFindReq(key = key))

  def createJob(req: JobCreateReq): Future[JobCreateResp] = askToJob[JobCreateResp](req)

  def updateJob(req: JobUpdateReq): Future[JobSchedulerResp] = askToJob[JobSchedulerResp](req)

  @inline private def askToJob[RESP](req: JobMessage)(implicit tag: ClassTag[RESP]): Future[RESP] =
    jobActor.ask[JobResponse](replyTo => CommandReply(req, replyTo)).mapTo[RESP]
}
