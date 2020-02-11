package mass.job.repository

import java.time.OffsetDateTime

import mass.data.CommonStatus
import mass.data.job._
import mass.slick.SlickProfile.api._

class JobScheduleTable(tag: Tag) extends Table[JobSchedule](tag, "job_schedule") {
  def key = column[String]("key")
  def job = column[JobItem]("job")
  def trigger = column[JobTrigger]("trigger")
  def description = column[String]("description")
  def runStatus = column[RunStatus]("run_status")
  def status = column[CommonStatus]("status")
  def creator = column[String]("creator")
  def createdAt = column[OffsetDateTime]("created_at")
  def scheduleCount = column[Long]("schedule_count")
  def lastScheduleStart = column[Option[OffsetDateTime]]("last_schedule_start")
  def lastScheduledAt = column[Option[OffsetDateTime]]("last_scheduled_at")

  def * =
    (
      key,
      job.?,
      trigger.?,
      description,
      runStatus,
      status,
      creator,
      createdAt,
      scheduleCount,
      lastScheduleStart,
      lastScheduledAt) <> ((JobSchedule.apply _).tupled, JobSchedule.unapply)
}

class JobLogTable(tag: Tag) extends Table[JobLog](tag, "job_log") {
  def id = column[String]("id", O.PrimaryKey, O.SqlTypeObjectId)
  def jobKey = column[String]("job_key")
  def startTime = column[OffsetDateTime]("start_time")
  def completionTime = column[Option[OffsetDateTime]]("completion_time")
  def completionStatus = column[RunStatus]("completion_status")
  def completionValue = column[Option[String]]("completion_value", O.SqlType("text"))
  def createdAt = column[OffsetDateTime]("created_at")

  def * =
    (id, jobKey, startTime, completionTime, completionStatus, completionValue, createdAt) <> ((JobLog.apply _).tupled, JobLog.unapply)
}
