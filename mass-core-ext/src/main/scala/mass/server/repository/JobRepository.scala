package mass.server.repository

import java.time.OffsetDateTime

import helloscala.common.types.ObjectId
import mass.scheduler.model.JobScheduleStatus.JobScheduleStatus
import mass.scheduler.model.{JobDetail, JobLog, JobSchedule, JobTrigger}
import mass.slick.PgProfile
import mass.slick.PgProfile.api._

import scala.concurrent.Future

object JobRepository {

  def apply(db: PgProfile.backend.DatabaseDef): JobRepository = new JobRepository(db)

}

class JobRepository private (db: PgProfile.backend.DatabaseDef) {

  import mass.scheduler.model.JobSchemas._

  def saveJobDetail(jobDetail: JobDetail): Future[Option[JobDetail]] =
    db.run(JobDetailRow returning JobDetailRow insertOrUpdate jobDetail)

  def findJobDetail(key: String): Future[Option[JobDetail]] =
    db.run(JobDetailRow.filter(_.key === key).result.headOption)

  def listJobDetail(): Future[Seq[JobDetail]] =
    db.run(JobDetailRow.result)

  def saveJobTrigger(jobTrigger: JobTrigger): Future[Option[JobTrigger]] =
    db.run(JobTriggerRow returning JobTriggerRow insertOrUpdate jobTrigger)

  def findJobTrigger(key: String): Future[Option[JobTrigger]] =
    db.run(JobTriggerRow.filter(_.key === key).result.headOption)

  def listJobTrigger(): Future[Seq[JobTrigger]] =
    db.run(JobTriggerRow.result)

  def insertJobSchedule(jobSchedule: JobSchedule): Future[JobSchedule] =
    db.run(JobScheduleRow returning JobScheduleRow += jobSchedule)

  def updateJobSchedule(id: ObjectId, status: JobScheduleStatus): Future[Int] =
    db.run(JobScheduleRow.filter(_.id === id).map(_.status).update(status))

  def insertJobLog(jobLog: JobLog): Future[JobLog] =
    db.run(JobLogRow returning JobLogRow += jobLog)

  def completionJobLog(
      id: ObjectId,
      completionTime: Option[OffsetDateTime],
      completionStatus: Option[Int],
      completionValue: Option[String]): Future[Int] =
    db.run(JobLogRow
      .filter(_.id === id)
      .map(r => (r.completionTime, r.completionStatus, r.completionValue))
      .update((completionTime, completionStatus, completionValue)))

}
