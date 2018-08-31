package mass.scheduler.repository
import java.time.OffsetDateTime

import helloscala.common.types.ObjectId
import mass.scheduler.model._
import mass.slick.SlickProfile.api._
import massmsg.CommonStatus
import slick.dbio.Effect
import slick.sql.{FixedSqlAction, FixedSqlStreamingAction, SqlAction}

import scala.concurrent.ExecutionContext

trait JobRepo {

  def page(req: JobPageReq)(implicit ec: ExecutionContext): DBIOAction[JobPageResp, NoStream, Effect.Read] = {
    val q = tJobDetail.filter(t => dynamicFilter(None))
    for {
      content <- q.sortBy(_.createdAt.desc).drop(req.offset).take(req.size).result
      total <- q.length.result
    } yield JobPageResp(content, total, req.page, req.size)
  }

  def saveJobDetail(jobDetail: JobDetail): FixedSqlAction[Option[JobDetail], NoStream, Effect.Write] =
    tJobDetail returning tJobDetail insertOrUpdate jobDetail

  def findJobDetail(
      key: String
  ): SqlAction[Option[JobDetail], NoStream, Effect.Read] =
    tJobDetail.filter(_.key === key).result.headOption

  def listJobDetail(): FixedSqlStreamingAction[Seq[JobDetail], JobDetail, Effect.Read] =
    tJobDetail.result

  def saveJobTrigger(jobTrigger: JobTrigger): FixedSqlAction[Option[JobTrigger], NoStream, Effect.Write] =
    tJobTrigger returning tJobTrigger insertOrUpdate jobTrigger

  def findJobTrigger(
      key: String
  ): SqlAction[Option[JobTrigger], NoStream, Effect.Read] =
    tJobTrigger.filter(_.key === key).result.headOption

  def listJobTrigger(): FixedSqlStreamingAction[Seq[JobTrigger], JobTrigger, Effect.Read] =
    tJobTrigger.result

  def insertJobSchedule(jobSchedule: JobSchedule): FixedSqlAction[JobSchedule, NoStream, Effect.Write] =
    tJobSchedule returning tJobSchedule += jobSchedule

  def updateJobSchedule(id: ObjectId, status: CommonStatus): FixedSqlAction[Int, NoStream, Effect.Write] =
    tJobSchedule.filter(_.id === id).map(_.status).update(status)

  def insertJobLog(jobLog: JobLog): FixedSqlAction[JobLog, NoStream, Effect.Write] =
    tJobLog returning tJobLog += jobLog

  def completionJobLog(
      id: ObjectId,
      completionTime: Option[OffsetDateTime],
      completionStatus: Option[Int],
      completionValue: Option[String]
  ): FixedSqlAction[Int, NoStream, Effect.Write] =
    tJobLog
      .filter(_.id === id)
      .map(r => (r.completionTime, r.completionStatus, r.completionValue))
      .update((completionTime, completionStatus, completionValue))

}

object JobRepo extends JobRepo
