package mass.job.repository

import java.time.OffsetDateTime

import helloscala.common.exception.HSNotFoundException
import helloscala.common.types.ObjectId
import helloscala.common.util.StringUtils
import mass.db.slick.SlickProfile
import mass.db.slick.SlickProfile.api._
import mass.job.util.JobZip
import mass.message.job._
import mass.model.CommonStatus
import mass.model.job._
import slick.sql.{ FixedSqlAction, FixedSqlStreamingAction }

import scala.concurrent.ExecutionContext

object JobRepo {
  def listJob(req: JobListReq): FixedSqlStreamingAction[Seq[JobSchedule], JobSchedule, Effect.Read] = {
    tJobSchedule
      .filter(t => dynamicFilterOr(Seq(StringUtils.option(req.key).map(key => t.key.? ilike s"$key%"))))
      .sortBy(_.createdAt.desc)
      .result
  }

  def save(req: JobCreateReq): DBIOAction[JobSchedule, NoStream, Effect.Write] = {
    val key = req.key.getOrElse(ObjectId.getString())
    val payload = JobSchedule.fromReq(key, req)
    tJobSchedule.returning(tJobSchedule) += payload
  }

  def save(jobZip: JobZip, creator: String = ""): DBIOAction[Vector[JobSchedule], NoStream, Effect.Write] = {
    val actions = jobZip.configs.map { config =>
      val jobItem = config.item
      val trigger = config.trigger
      val schedule = JobSchedule.fromJobItemAndTrigger(config.key.getOrElse(ObjectId.getString()), jobItem, trigger)
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
        val payload = schedule.mergeUpdate(req)
        q.update(payload).map(_ => payload)
      case None => DBIO.failed(HSNotFoundException(s"Job：${req.key} Not Found."))
    }
  }

  def updateJobTriggerLog(key: String, triggerLog: TriggerLog): FixedSqlAction[Int, NoStream, Effect.Write] = {
    tJobSchedule.filter(_.key === key).map(_.triggerLog).update(Some(triggerLog))
  }

  def insertJobLog(jobLog: TriggerLog): DBIOAction[Unit, NoStream, Effect.Write] = {
    DBIO.seq(tJobLog += jobLog, updateJobTriggerLog(jobLog.jobKey, jobLog))
  }

  def completionJobLog(
      id: String,
      completionTime: OffsetDateTime,
      completionStatus: RunStatus,
      completionValue: String)(
      implicit ec: ExecutionContext): DBIOAction[Int, SlickProfile.api.NoStream, Effect.Write with Effect.Read] = {
    val u = tJobLog
      .filter(_.id === id)
      .map(r => (r.completionTime, r.completionStatus, r.completionValue))
      .update((Some(completionTime), completionStatus, Some(completionValue)))

    u.flatMap {
      case 1 =>
        tJobLog.filter(_.id === id).result.headOption.flatMap {
          case Some(log) => updateJobTriggerLog(log.jobKey, log)
          case _         => DBIO.successful(0)
        }
      case _ => DBIO.successful(0)
    }
  }

  def listValid() = {
    tJobSchedule.filter(t => t.status === CommonStatus.ENABLE).result
  }
}
