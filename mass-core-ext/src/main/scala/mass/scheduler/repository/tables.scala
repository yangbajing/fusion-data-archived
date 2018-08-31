package mass.scheduler.repository

import java.time.OffsetDateTime

import com.fasterxml.jackson.databind.JsonNode
import helloscala.common.types.ObjectId
import mass.scheduler.model.{JobDetail, JobLog, JobSchedule, JobTrigger}
import mass.slick.SlickProfile.api._
import massmsg.CommonStatus

import scala.concurrent.duration.FiniteDuration

class JobTriggerTable(tag: Tag) extends Table[JobTrigger](tag, "job_trigger") {
  def key = column[String]("key", O.PrimaryKey)

  def cronExpress = column[Option[String]]("cron_express")

  def duration = column[Option[FiniteDuration]]("duration")

  def repeat = column[Option[Int]]("repeat")

  def startTime = column[Option[OffsetDateTime]]("start_time")

  def endTime = column[Option[OffsetDateTime]]("end_time")

  def conf = column[Option[JsonNode]]("conf")

  def createdAt = column[OffsetDateTime]("created_at")

  def * =
    (key, cronExpress, duration, repeat, startTime, endTime, conf, createdAt)
      .mapTo[JobTrigger]
}

class JobDetailTable(tag: Tag) extends Table[JobDetail](tag, "job_detail") {
  def key = column[String]("key", O.PrimaryKey)

  def data = column[Map[String, String]]("data")

  def conf = column[Option[JsonNode]]("conf")

  def createdAt = column[OffsetDateTime]("created_at")

  def * = (key, data, conf, createdAt).mapTo[JobDetail]
}

class JobScheduleTable(tag: Tag) extends Table[JobSchedule](tag, "job_schedule") {
  def id = column[ObjectId]("id", O.PrimaryKey, O.SqlTypeObjectId)

  def detailKey = column[String]("detail_key")

  def triggerKey = column[String]("trigger_key")

  def status = column[CommonStatus]("status")

  def createdAt = column[OffsetDateTime]("created_at")

  def * = (id, detailKey, triggerKey, status, createdAt).mapTo[JobSchedule]
}

class JobLogTable(tag: Tag) extends Table[JobLog](tag, "job_log") {
  def id = column[ObjectId]("id", O.PrimaryKey, O.SqlTypeObjectId)

  def jobId = column[ObjectId]("job_id", O.SqlTypeObjectId)

  def startTime = column[OffsetDateTime]("start_time")

  def completionTime = column[Option[OffsetDateTime]]("completion_time")

  def completionStatus = column[Option[Int]]("completion_status")

  def completionValue =
    column[Option[String]]("completion_value", O.SqlType("text"))

  def createdAt = column[OffsetDateTime]("created_at")

  def * =
    (id, jobId, startTime, completionTime, completionStatus, completionValue, createdAt).mapTo[JobLog]
}
