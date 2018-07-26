package mass.scheduler.model

import java.time.OffsetDateTime

import helloscala.common.types.ObjectId
import mass.scheduler.model.JobScheduleStatus.JobScheduleStatus
import mass.slick.SlickProfile.api._
import org.json4s.JValue

import scala.concurrent.duration.FiniteDuration

object JobSchemas extends JobSchemas

abstract class JobSchemas {

  class JobDetailRow(tag: Tag) extends Table[JobDetail](tag, "job_detail") {
    def key = column[String]("key", O.PrimaryKey)

    def data = column[Map[String, String]]("data")

    def conf = column[Option[JValue]]("conf")

    def createdAt = column[OffsetDateTime]("created_at")

    def * = (key, data, conf, createdAt).mapTo[JobDetail]
  }

  val JobDetailRow = TableQuery[JobDetailRow]

  class JobTriggerRow(tag: Tag) extends Table[JobTrigger](tag, "job_trigger") {
    def key = column[String]("key", O.PrimaryKey)

    def cronExpress = column[Option[String]]("cron_express")

    def duration = column[Option[FiniteDuration]]("duration")

    def repeat = column[Option[Int]]("repeat")

    def startTime = column[Option[OffsetDateTime]]("start_time")

    def endTime = column[Option[OffsetDateTime]]("end_time")

    def conf = column[Option[JValue]]("conf")

    def createdAt = column[OffsetDateTime]("created_at")

    def * =
      (key, cronExpress, duration, repeat, startTime, endTime, conf, createdAt)
        .mapTo[JobTrigger]
  }

  val JobTriggerRow = TableQuery[JobTriggerRow]

  implicit val JobScheduleStatusColumnType = MappedColumnType
    .base[JobScheduleStatus, Int](_.id, id => JobScheduleStatus(id))

  class JobScheduleRow(tag: Tag)
      extends Table[JobSchedule](tag, "job_schedule") {
    def id = column[ObjectId]("id", O.PrimaryKey, O.SqlTypeObjectId)

    def detailKey = column[String]("detail_key")

    def triggerKey = column[String]("trigger_key")

    def status = column[JobScheduleStatus]("status")

    def createdAt = column[OffsetDateTime]("created_at")

    def * = (id, detailKey, triggerKey, status, createdAt).mapTo[JobSchedule]
  }

  val JobScheduleRow = TableQuery[JobScheduleRow]

  class JobLogRow(tag: Tag) extends Table[JobLog](tag, "job_log") {
    def id = column[ObjectId]("id", O.PrimaryKey, O.SqlTypeObjectId)

    def jobId = column[ObjectId]("job_id", O.SqlTypeObjectId)

    def startTime = column[OffsetDateTime]("start_time")

    def completionTime = column[Option[OffsetDateTime]]("completion_time")

    def completionStatus = column[Option[Int]]("completion_status")

    def completionValue =
      column[Option[String]]("completion_value", O.SqlType("text"))

    def createdAt = column[OffsetDateTime]("created_at")

    def * =
      (id,
       jobId,
       startTime,
       completionTime,
       completionStatus,
       completionValue,
       createdAt).mapTo[JobLog]
  }

  val JobLogRow = TableQuery[JobLogRow]

}
