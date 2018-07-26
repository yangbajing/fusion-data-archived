package mass.scheduler.web.route

import java.nio.file.Files

import akka.http.scaladsl.server.Route
import com.typesafe.scalalogging.StrictLogging
import mass.http.AbstractRoute
import mass.scheduler.SchedulerSystem
import mass.scheduler.business.service.JobService

class JobRoute(schedulerSystem: SchedulerSystem)
    extends AbstractRoute
    with StrictLogging {
  import schedulerSystem.massSystem.system.dispatcher

  private val jobService = new JobService(schedulerSystem)

  override def route: Route = pathPrefix("job") {
    uploadJobRoute
  }

  def uploadJobRoute: Route = pathPost("upload_job") {
    storeUploadedFile("job",
                      createTempFileFunc(schedulerSystem.massSystem.tempDir)) {
      case (fileInfo, file) =>
        val future = jobService
          .uploadJob(file, fileInfo.fileName, fileInfo.contentType.charset)
          .andThen { case _ => Files.deleteIfExists(file.toPath) }
        onSuccess(future) { result =>
          objectComplete(result)
        }
    }
  }

}
