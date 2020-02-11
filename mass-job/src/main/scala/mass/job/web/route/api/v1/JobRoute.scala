package mass.job.web.route.api.v1

import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.page.Page
import mass.http.AbstractRoute
import mass.job.service.Services
import mass.message.job._

class JobRoute(services: Services) extends AbstractRoute with StrictLogging {
  private val pagePDM = ('page.as[Int].?(Page.DEFAULT_PAGE), 'size.as[Int].?(Page.DEFAULT_SIZE), 'key.?(""))

  override def route: Route = pathPrefix("job") {
    pathEndOrSingleSlash {
      createJobRoute ~
      updateJobRoute
    } ~
    pageRoute ~
    itemByKeyRoute ~
    uploadJobZipRoute ~
    optionRoute ~
    uploadFileRoute
  }

  def createJobRoute: Route = post {
    entity(jacksonAs[JobCreateReq]) { req =>
      futureComplete(services.createJob(req))
    }
  }

  def updateJobRoute: Route = put {
    entity(jacksonAs[JobUpdateReq]) { req =>
      futureComplete(services.updateJob(req))
    }
  }

  def itemByKeyRoute: Route = pathGet("item" / Segment) { key =>
    futureComplete(services.findItemByKey(key))
  }

  def pageRoute: Route = pathGet("page") {
    parameters(pagePDM).as(JobPageReq.apply _) { req =>
      futureComplete(services.page(req))
    }
  }

  def uploadJobZipRoute: Route = pathPost("upload_job") {
    extractExecutionContext { implicit ec =>
      storeUploadedFile("job", createTempFileFunc(services.jobSystem.massSystem.tempDirectory)) {
        case (fileInfo, file) =>
          futureComplete(services.uploadJobOnZip(fileInfo, file))
      }
    }
  }

  def uploadFileRoute: Route = pathPost("upload_file") {
    extractExecutionContext { implicit ec =>
      uploadedFiles(createTempFileFunc(services.jobSystem.massSystem.tempDirectory)) { list =>
        futureComplete(services.uploadFiles(list))
      }
    }
  }

  def optionRoute: Route = pathPrefix("option" / "all") {
    futureComplete(services.listOption())
  }
}
