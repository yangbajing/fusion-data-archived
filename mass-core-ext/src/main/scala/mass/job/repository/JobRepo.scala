package mass.job.repository

import java.time.OffsetDateTime

import helloscala.common.exception.{ HSBadRequestException, HSNotFoundException }
import helloscala.common.types.ObjectId
import helloscala.common.util.StringUtils
import mass.job.util.JobZip
import mass.message.job._
import mass.model.CommonStatus
import mass.model.job._
import mass.slick.SlickProfile.api._
import slick.sql.{ FixedSqlAction, FixedSqlStreamingAction, SqlAction }

import scala.concurrent.ExecutionContext

object JobRepo {
  def listJob(req: JobListReq): FixedSqlStreamingAction[Seq[JobSchedule], JobSchedule, Effect.Read] = {
    tJobSchedule
      .filter(t => dynamicFilterOr(Seq(StringUtils.option(req.key).map(key => t.key ilike s"$key%"))))
      .sortBy(_.createdAt.desc)
      .result
  }

  def save(req: JobCreateReq): DBIOAction[JobSchedule, NoStream, Effect.Write] = {
    val key = req.key.getOrElse(ObjectId.getString())
    val payload = JobSchedule(
      key,
      req.item,
      req.trigger,
      req.description.getOrElse(""),
      RunStatus.JOB_NORMAL,
      CommonStatus.ENABLE,
      creator = "",
      OffsetDateTime.now())
    tJobSchedule.returning(tJobSchedule) += payload
  }

  def save(jobZip: JobZip, creator: String = "")(
      implicit ec: ExecutionContext): DBIOAction[Vector[JobSchedule], NoStream, Effect.Write] = {
    val now = OffsetDateTime.now()
    val actions = jobZip.configs.map { config =>
      val jobItem = config.item
      val trigger = config.trigger
      val schedule = JobSchedule(
        config.key.getOrElse(ObjectId.getString()),
        jobItem,
        trigger,
        s"triggerType: ${trigger.triggerType}",
        RunStatus.JOB_NORMAL,
        CommonStatus.ENABLE,
        creator = "",
        createdAt = now)
      tJobSchedule.returning(tJobSchedule) += schedule
    }
    DBIO.sequence(actions)
  }

  def page(req: JobPageReq)(implicit ec: ExecutionContext): DBIOAction[JobPageResp, NoStream, Effect.Read] = {
    val q = filterWhere(req)
    for {
      content <- q.sortBy(_.createdAt.desc).drop(req.offset).take(req.size).result
      total <- q.length.result
    } yield JobPageResp(content, total, req.page, req.size)
  }

  def filterWhere(req: JobPageReq) = {
    tJobSchedule.filter(t => dynamicFilter(StringUtils.option(req.key).map(key => t.key ilike s"$key%")))
  }

  def findJob(key: String): DBIOAction[Option[JobSchedule], NoStream, Effect.Read with Effect.Read] =
    tJobSchedule.filter(t => t.key === key).result.headOption

  def insertJobSchedule(jobSchedule: JobSchedule): FixedSqlAction[JobSchedule, NoStream, Effect.Write] =
    tJobSchedule returning tJobSchedule += jobSchedule

  def updateJobSchedule(req: JobUpdateReq)(
      implicit ec: ExecutionContext): DBIOAction[JobSchedule, NoStream, Effect.Read with Effect.Write] = {
    val q = tJobSchedule.filter(t => t.key === req.key)
    q.result.headOption.flatMap {
      case Some(schedule) =>
        val payload = schedule.copy(
          item = req.item.getOrElse(schedule.item),
          trigger = req.trigger.getOrElse(schedule.trigger),
          description = req.description.getOrElse(schedule.description),
          status = req.status.getOrElse(schedule.status))
        q.update(payload).map(_ => payload)
      case None => DBIO.failed(HSNotFoundException(s"Job：${req.key} Not Found."))
    }
  }

  def updateJobItemScheduledAt(
      key: String,
      status: RunStatus,
      lastScheduleStart: OffsetDateTime): SqlAction[Int, NoStream, Effect] = {
    import mass.slick.SlickProfile.plainApi._
    sqlu"""update job_schedule set schedule_count = schedule_count + 1, status = $status where key = $key and last_schedule_start = $lastScheduleStart"""
  }

  def updateJobRunStatus(key: String, runStatus: RunStatus): FixedSqlAction[Int, NoStream, Effect.Write] = {
    tJobSchedule.filter(_.key === key).map(_.runStatus).update(runStatus)
  }

  def updateJobRunStatusByLogId(
      logId: String,
      runStatus: RunStatus,
      lastScheduledAt: OffsetDateTime): FixedSqlAction[Int, NoStream, Effect.Write] = {
    tJobSchedule
      .filter(t => t.key in tJobLog.filter(_.id === logId).map(_.jobKey))
      .map(t => (t.runStatus, t.lastScheduledAt))
      .update((runStatus, Some(lastScheduledAt)))
  }

  def insertJobLog(jobLog: JobLog): DBIOAction[Unit, NoStream, Effect.Write] = {
    DBIO.seq(tJobLog += jobLog, updateJobItemScheduledAt(jobLog.jobKey, jobLog.completionStatus, jobLog.startTime))
  }

  def completionJobLog(
      id: String,
      completionTime: OffsetDateTime,
      completionStatus: RunStatus,
      completionValue: String): DBIOAction[Unit, NoStream, Effect.Write] = {
    DBIO.seq(
      tJobLog
        .filter(_.id === id)
        .map(r => (r.completionTime, r.completionStatus, r.completionValue))
        .update((Some(completionTime), completionStatus, Some(completionValue))),
      updateJobRunStatusByLogId(id, completionStatus, completionTime))
  }
}
