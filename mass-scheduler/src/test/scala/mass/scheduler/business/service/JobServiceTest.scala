package mass.scheduler.business.service

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import mass.scheduler.SchedulerSpec
import mass.scheduler.model.JobUploadJobReq

class JobServiceTest extends SchedulerSpec {

  "JobService" should {
    "uploadJob" in {
      val file = Paths.get("/opt/Documents/SSL.zip").toFile
      val fileName = "SSL.zip"
      val result =
        JobService
          .uploadJob(schedulerSystem.conf, JobUploadJobReq(file, fileName, StandardCharsets.UTF_8))
          .futureValue
      println(result)
    }
  }

}
