package mass.job.repository

import java.time.OffsetDateTime

import mass.core.job.JobConstants
import mass.model.CommonStatus
import mass.model.job._
import mass.db.slick.SlickProfile.api._
import slick.collection.heterogeneous.HNil

import scala.concurrent.duration.FiniteDuration

class JobScheduleTable(tag: Tag) extends Table[JobSchedule](tag, "job_schedule") {
  def key = column[String]("key")
  def program = column[Program]("program")
  def programOptions = column[Seq[String]]("program_options")
  def programMain = column[String]("program_main")
  def programArgs = column[Seq[String]]("program_args")
  def programVersion = column[String]("program_version")
  def resources = column[Map[String, String]]("resources")
  def data = column[Map[String, String]]("data")
  def description = column[Option[String]]("description")
  def dependentJobKeys = column[Seq[String]]("dependent_job_keys")
  def name = column[Option[String]]("name")
  def triggerType = column[TriggerType]("trigger_type")
  def triggerEvent = column[String]("trigger_event")
  def startTime = column[OffsetDateTime]("start_time")
  def endTime = column[Option[OffsetDateTime]]("end_time")
  def repeat = column[Int]("repeat", O.Default(JobConstants.TRIGGER_REPEAT))
  def interval = column[FiniteDuration]("interval", O.Default(JobConstants.TRIGGER_INTERVAL))
  def cronExpress = column[String]("String")
  def failedRetries = column[Int]("failed_retries", O.Default(0))
  def timeout = column[FiniteDuration]("timeout", O.Default(JobConstants.RUN_TIMEOUT))
  def alarmEmails = column[Seq[String]]("alarm_emails")
  def status = column[CommonStatus]("status")
  def creator = column[String]("creator")
  def createdAt = column[OffsetDateTime]("created_at")
  def scheduleCount = column[Long]("schedule_count")
  def triggerLog = column[Option[TriggerLog]]("trigger_log")

  def * =
    (key ::
    program ::
    programOptions ::
    programMain ::
    programArgs ::
    programVersion ::
    resources ::
    data ::
    description ::
    dependentJobKeys ::
    name ::
    triggerType ::
    triggerEvent ::
    startTime ::
    endTime ::
    repeat ::
    interval ::
    cronExpress ::
    failedRetries ::
    timeout ::
    alarmEmails ::
    status ::
    creator ::
    createdAt ::
    scheduleCount ::
    triggerLog :: HNil).mapTo[JobSchedule]
}

class TriggerLogTable(tag: Tag) extends Table[TriggerLog](tag, "job_trigger_log") {
  def id = column[String]("id", O.PrimaryKey, O.SqlTypeObjectId)
  def jobKey = column[String]("job_key")
  def startTime = column[OffsetDateTime]("start_time")
  def completionTime = column[Option[OffsetDateTime]]("completion_time")
  def completionStatus = column[RunStatus]("completion_status")
  def completionValue = column[Option[String]]("completion_value", O.SqlType("text"))
  def createdAt = column[OffsetDateTime]("created_at")

  def * =
    (id, jobKey, startTime, completionTime, completionStatus, completionValue, createdAt) <> ((TriggerLog.apply _).tupled, TriggerLog.unapply)
}
