package mass.server.repository

import java.time.OffsetDateTime

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helloscala.common.test.HelloscalaSpec
import mass.core.MassSystem
import mass.scheduler.model.{JobDetail, JobTrigger}
import mass.server.MassSystemExtension
import org.scalatest.BeforeAndAfterAll

class JobRepositoryTest
    extends TestKit(ActorSystem("test"))
    with HelloscalaSpec
    with BeforeAndAfterAll {
  private var massSystem: MassSystemExtension = _

  override protected def beforeAll(): Unit = {
    MassSystem(system)
    massSystem = MassSystemExtension.instance
  }

  "JobRepositoryTest" should {

    "saveJobDetail" in {
      val jobRepository = JobRepository(massSystem.slickDatabase)
      val jobDetail = JobDetail("key",
                                Map("className" -> "java.lang.String"),
                                None,
                                OffsetDateTime.now())
      val result = jobRepository.saveJobDetail(jobDetail).futureValue
      println(s"saveJobDetail: $result")
    }

    "saveJobTrigger" in {
      val jobRepository = JobRepository(massSystem.slickDatabase)
      val jobTrigger = JobTrigger("key",
                                  Some("10 * * * * ?"),
                                  None,
                                  None,
                                  None,
                                  None,
                                  None,
                                  OffsetDateTime.now())
      val result = jobRepository.saveJobTrigger(jobTrigger).futureValue
      println(s"saveJobTrigger: $result")
    }

  }

}
