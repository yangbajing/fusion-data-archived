package mass.job.service

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files

import akka.actor.{ ActorRef, Props }
import akka.http.scaladsl.server.directives.FileInfo
import akka.pattern._
import akka.util.Timeout
import mass.job.JobSystem
import mass.job.model.{ JobUploadFilesReq, JobUploadJobReq }
import mass.job.service.job.JobActor
import mass.message.job._

import scala.collection.immutable
import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }

class Services(val jobSystem: JobSystem, propsList: Iterable[(Props, Symbol)]) {
  implicit val timeout: Timeout = Timeout(10.seconds)
  val aggregate: ActorRef = jobSystem.system.actorOf(JobAggregate.props(propsList), JobAggregate.name.name)

  def listOption(): Future[JobGetAllOptionResp] =
    (aggregate ? JobActor.message(JobGetAllOptionReq())).mapTo[JobGetAllOptionResp]

  def uploadFiles(list: immutable.Seq[(FileInfo, File)])(implicit ec: ExecutionContext): Future[JobUploadFilesResp] = {
    (aggregate ? JobActor.message(JobUploadFilesReq(list))).mapTo[JobUploadFilesResp].andThen {
      case _ => list.foreach { case (_, file) => Files.deleteIfExists(file.toPath) }
    }
  }

  def uploadJobOnZip(fileInfo: FileInfo, file: File)(implicit ec: ExecutionContext): Future[JobUploadJobResp] = {
    val msg = JobUploadJobReq(
      file,
      fileInfo.fileName,
      fileInfo.contentType.charsetOption.map(_.nioCharset()).getOrElse(StandardCharsets.UTF_8))
    (aggregate ? JobActor.message(msg)).mapTo[JobUploadJobResp].andThen { case _ => Files.deleteIfExists(file.toPath) }
  }

  def updateTrigger(req: JobUpdateReq): Future[JobFindResp] =
    (aggregate ? JobActor.message(req)).mapTo[JobFindResp]

  def page(req: JobPageReq): Future[JobPageResp] = (aggregate ? JobActor.message(req)).mapTo[JobPageResp]

  def findItemByKey(key: String): Future[JobFindResp] =
    (aggregate ? JobActor.message(JobFindReq(key = key))).mapTo[JobFindResp]

  def createJob(req: JobCreateReq): Future[JobCreateResp] =
    (aggregate ? JobActor.message(req)).mapTo[JobCreateResp]

  def updateJob(req: JobUpdateReq): Future[JobFindResp] = (aggregate ? JobActor.message(req)).mapTo[JobFindResp]
}
