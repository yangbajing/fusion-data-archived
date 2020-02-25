package mass.server.repository

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import fusion.json.jackson.Jackson
import mass.db.slick.PgProfile.api._
import mass.extension.MassSystem
import mass.job.db.model.QrtzModels
import mass.model.job.JobTrigger
import org.scalatest.wordspec.AnyWordSpecLike

class JobRepoTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  private val massSystem: MassSystem = MassSystem(system)

  "JobRepoTest" should {
    val db = massSystem.sqlSystem.db

    "filterWhere" in {
      val action = QrtzModels.QrtzJobDetailsModel.sortBy(_.createdAt.desc).take(30).result
      action.statements.foreach(println)

      val result = db.run(action).futureValue
      println(result)
    }

    "log" in {
      QrtzModels.QrtzTriggerLogModel.schema.createStatements.foreach(println)
    }
  }

  "JSON" should {
    "trigger" in {
      val jstr = """{"key":"kettle","triggerType":"CRON","startTime":"2018-09-12T13:00:11+08","endTime":null}"""
      val trigger = Jackson.defaultObjectMapper.readValue[JobTrigger](jstr)
      println(trigger)
    }
  }
}
