package mass.server.repository

import fusion.inject.guice.testkit.GuiceApplicationTestkit
import fusion.json.jackson.ScalaObjectMapper
import mass.db.slick.{ PgProfile, SqlComponent }
import mass.job.db.model.QrtzModels
import mass.model.job.JobTrigger
import org.scalatest.wordspec.AnyWordSpecLike

class JobRepoTest extends GuiceApplicationTestkit with AnyWordSpecLike {
  private val qrtzModels = injectInstance[QrtzModels]
  private val objectMapper = injectInstance[ScalaObjectMapper]
  private val sqlSystem = injectInstance[SqlComponent]
  private val profile = injectInstance[PgProfile]
  import profile.api._

  "JobRepoTest" should {
    val db = sqlSystem.db

    "filterWhere" in {
      val action = qrtzModels.QrtzJobDetailsModel.sortBy(_.createdAt.desc).take(30).result
      action.statements.foreach(println)

      val result = db.run(action).futureValue
      println(result)
    }

    "log" in {
      qrtzModels.QrtzTriggerLogModel.schema.createStatements.foreach(println)
    }
  }

  "JSON" should {
    "trigger" in {
      val jstr = """{"key":"kettle","triggerType":"CRON","startTime":"2018-09-12T13:00:11+08","endTime":null}"""
      val trigger = objectMapper.readValue[JobTrigger](jstr)
      println(trigger)
    }
  }
}
