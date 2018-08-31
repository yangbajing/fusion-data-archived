package mass.server.repository

import java.time.OffsetDateTime

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helloscala.common.test.HelloscalaSpec
import mass.core.MassSystem
import mass.scheduler.model.{JobDetail, JobTrigger}
import mass.server.MassSystemExtension
import org.scalatest.BeforeAndAfterAll

class JobRepoTest extends TestKit(ActorSystem("test")) with HelloscalaSpec with BeforeAndAfterAll {

  private val massSystem: MassSystemExtension = MassSystem(system)
    .as[MassSystemExtension]

  "JobRepositoryTest" should {
    val db = massSystem.slickDatabase
    import mass.scheduler.repository.JobRepo._

    "saveJobDetail" in {
      val jobDetail = JobDetail("key", Map("className" -> "java.lang.String"), None, OffsetDateTime.now())
      val result = db.run(saveJobDetail(jobDetail)).futureValue
      println(s"saveJobDetail: $result")
    }

    "saveJobTrigger" in {
      val jobTrigger = JobTrigger("key", Some("10 * * * * ?"), None, None, None, None, None, OffsetDateTime.now())
      val result = db.run(saveJobTrigger(jobTrigger)).futureValue
      println(s"saveJobTrigger: $result")
    }

  }

}
