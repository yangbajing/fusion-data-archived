package mass.server.repository

import java.time.{ LocalDateTime, OffsetDateTime }

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import fusion.json.jackson.Jackson
import helloscala.common.util.TimeUtils
import mass.extension.MassSystem
import mass.job.repository._
import mass.message.job.JobPageReq
import mass.slick.SlickProfile.api._
import org.scalatest.wordspec.AnyWordSpecLike

class JobRepoTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  private val massSystem: MassSystem = MassSystem(system)

  "JobRepositoryTest" should {
    val db = massSystem.sqlManager.slickDatabase

    "saveJobDetail" in {
//      val jobDetail = JobItem("key", Map("className" -> "java.lang.String"), None, OffsetDateTime.now())
//      val result = db.run(jobRepo.saveJobDetail(jobDetail)).futureValue
//      println(s"saveJobDetail: $result")
    }

    "saveJobTrigger" in {
//      val jobTrigger = JobTrigger("key", Some("10 * * * * ?"), None, None, None, None, None, OffsetDateTime.now())
//      val result = db.run(jobRepo.saveJobTrigger(jobTrigger)).futureValue
//      println(s"saveJobTrigger: $result")
    }

    "filterWhere" in {
      val req = JobPageReq(page = 1, size = 30)
      val q = JobRepo.filterWhere(req)
      val action = q.sortBy(_.createdAt.desc).drop(req.offset).take(req.size).result
      action.statements.foreach(println)
    }
  }

  "JSON" should {
    "trigger" in {
      val jstr = """{"key":"kettle","triggerType":1,"startTime":"2018-09-12T13:00:11.459Z","endTime":null}"""
      val jnode = Jackson.readTree(jstr)
      println(jnode)
//      val trigger = Jackson.treeToValue[JobTrigger](jnode)
//      println(trigger)
      val odt = Jackson.treeToValue[LocalDateTime](jnode.get("startTime"))
      println(odt.atOffset(TimeUtils.ZONE_CHINA_OFFSET))
      println(odt.toString)
      println(odt.format(TimeUtils.formatterDateTime))
      println(OffsetDateTime.of(2018, 9, 12, 13, 0, 11, 0, TimeUtils.ZONE_CHINA_OFFSET))
    }
  }
}
