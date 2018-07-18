package mass.scheduler.business.service

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import mass.scheduler.SchedulerSpec

class JobServiceTest extends SchedulerSpec {

  "JobService" should {
    "uploadJob" in {
      val job = new JobService(schedulerSystem)
      val file = Paths.get("/opt/Documents/SSL.zip").toFile
      val fileName = "SSL.zip"
      val result = job.uploadJob(file, fileName, StandardCharsets.UTF_8).futureValue
      println(result)
    }
  }

}
