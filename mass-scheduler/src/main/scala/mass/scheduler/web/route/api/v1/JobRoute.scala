package mass.scheduler.web.route.api.v1

import java.nio.file.Files

import akka.http.scaladsl.server.Route
import akka.pattern._
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.page.Page
import mass.http.AbstractRoute
import mass.scheduler.boot.Services
import mass.scheduler.business.actors.JobActor
import mass.scheduler.model.{JobPageReq, JobUploadJobReq}

import scala.concurrent.duration._

class JobRoute(services: Services) extends AbstractRoute with StrictLogging {
  implicit val timeout = Timeout(10.seconds)

  override def route: Route = pathPrefix("job") {
    pageRoute ~
      uploadJobRoute
  }

  val pagePDM = ('page.as[Int].?(Page.DEFAULT_PAGE), 'size.as[Int].?(Page.DEFAULT_SIZE))

  def pageRoute: Route = pathGet("page") {
    parameters(pagePDM).as(JobPageReq) { req =>
      futureComplete(services.master ? (JobActor.job -> req))
    }
  }

  def uploadJobRoute: Route = pathPost("upload_job") {
    storeUploadedFile("job", createTempFileFunc(services.schedulerSystem.massSystem.tempDir)) {
      case (fileInfo, file) =>
        import services.ec
        val future =
          (services.master ? JobUploadJobReq(file, fileInfo.fileName, fileInfo.contentType.charset))
            .andThen { case _ => Files.deleteIfExists(file.toPath) }
        onSuccess(future) { result =>
          objectComplete(result)
        }
    }
  }

}
