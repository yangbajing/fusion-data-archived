package mass.server.repository

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import fusion.json.jackson.Jackson
import mass.db.slick.SlickProfile.api._
import mass.extension.MassSystem
import mass.job.repository._
import mass.message.job.JobPageReq
import mass.model.job.JobTrigger
import org.scalatest.wordspec.AnyWordSpecLike

class JobRepoTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  private val massSystem: MassSystem = MassSystem(system)

  "JobRepoTest" should {
    val db = massSystem.sqlManager.slickDb

    "filterWhere" in {
      val req = JobPageReq(page = 1, size = 30)
      val q = JobRepo.filterWhere(req)
      val action = q.sortBy(_.createdAt.desc).drop(req.offset).take(req.size).result
      action.statements.foreach(println)

      val result = db.run(action).futureValue
      println(result)
    }
  }

  "JSON" should {
    "trigger" in {
      val jstr = """{"key":"kettle","triggerType":1,"startTime":"2018-09-12T13:00:11+08","endTime":null}"""
      val trigger = Jackson.defaultObjectMapper.readValue(jstr, classOf[JobTrigger])
      println(trigger)
    }
  }
}
