package mass.job.route.api.v1

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.StrictLogging
import fusion.common.FusionProtocol
import fusion.http.server.{ AbstractRoute, JacksonDirectives }
import mass.common.page.Page
import mass.extension.MassCore
import mass.job.service.Services
import mass.message.job._

class JobRoute()(implicit system: ActorSystem[FusionProtocol.Command])
    extends AbstractRoute
    with JacksonDirectives
    with StrictLogging {
  private val pagePDM = ('page.as[Int].?(Page.DEFAULT_PAGE), Symbol("size").as[Int].?(Page.DEFAULT_SIZE), 'key.?)
  private val jobService = Services(system).jobService

  override def route: Route = pathPrefix("job") {
    createJobRoute ~
    updateJobRoute ~
    pageRoute ~
    itemByKeyRoute ~
    uploadJobZipRoute ~
    optionAllRoute ~
    uploadFileRoute
  }

  def createJobRoute: Route = pathPost("create") {
    entity(jacksonAs[JobCreateReq]) { req =>
      futureComplete(jobService.createJob(req))
    }
  }

  def updateJobRoute: Route = pathPost("update") {
    entity(jacksonAs[JobUpdateReq]) { req =>
      futureComplete(jobService.updateJob(req))
    }
  }

  def itemByKeyRoute: Route = pathGet("item" / Segment) { key =>
    futureComplete(jobService.findItemByKey(key))
  }

  def pageRoute: Route = pathGet("page") {
    parameters(pagePDM).as(JobPageReq.apply _) { req =>
      futureComplete(jobService.page(req))
    }
  }

  def uploadJobZipRoute: Route = pathPost("upload_job") {
    extractExecutionContext { implicit ec =>
      storeUploadedFile("job", createTempFileFunc(MassCore(system).tempDirectory)) {
        case (fileInfo, file) =>
          futureComplete(jobService.uploadJobOnZip(fileInfo, file.toPath))
      }
    }
  }

  def uploadFileRoute: Route = pathPost("upload_file") {
    extractExecutionContext { implicit ec =>
      uploadedFiles(createTempFileFunc(MassCore(system).tempDirectory)) { list =>
        futureComplete(jobService.uploadFiles(list))
      }
    }
  }

  def optionAllRoute: Route = pathGet("option_all") {
    futureComplete(jobService.listOption())
  }
}
