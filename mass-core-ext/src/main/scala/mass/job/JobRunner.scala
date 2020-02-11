package mass.job

import java.time.OffsetDateTime

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.exception.HSNotFoundException
import helloscala.common.types.ObjectId
import helloscala.common.util.StringUtils
import mass.core.job.{ SchedulerContext, SchedulerJob }
import mass.data.job.{ JobLog, RunStatus }
import mass.job.repository.JobRepo

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

object JobRunner extends StrictLogging {
  def execute(jobSystem: JobSystem, key: String, extData: Map[String, String], jobClass: String): Unit = {
    val db = jobSystem.massSystem.sqlManager
    val logId = ObjectId.getString()
    val createdAt = OffsetDateTime.now()

    val log =
      JobLog(logId, key, createdAt, None, RunStatus.JOB_RUNNING, None, createdAt)
    logger.debug(s"作业开始执行，logId: $logId")
    db.runTransaction(JobRepo.insertJobLog(log))

    val clz = Class.forName(jobClass)
    if (classOf[SchedulerJob].isAssignableFrom(clz)) {
      implicit val ec: ExecutionContext = jobSystem.executionContext
      db.run(JobRepo.findJob(key))
        .flatMap {
          case Some(schedule) =>
            val jobItem = schedule.item.get
            val data = jobItem.data ++ extData
            val ctx = SchedulerContext(key, jobItem, data, jobSystem.system)
            val job = clz.newInstance().asInstanceOf[SchedulerJob]
            // TODO 超时控制、失败重试、失败告警
            job.run(ctx)
          case _ =>
            Future.failed(HSNotFoundException(s"作业：$key 未找到"))
        }
        .onComplete { v =>
          val completionTime = OffsetDateTime.now()
          v match {
            case Success(msg) =>
              logger.info(s"作业执行成功，logId: $logId")
              db.runTransaction(JobRepo.completionJobLog(logId, completionTime, RunStatus.JOB_OK, msg.toString))
            case Failure(e) =>
              logger.error(s"作业执行失败，logId: $logId。${e.getMessage}")
              db.runTransaction(
                JobRepo.completionJobLog(logId, completionTime, RunStatus.JOB_FAILURE, StringUtils.toString(e)))
          }
        }
    } else {
      val msg = s"未知的作业类型：$jobClass，需要 ${classOf[SchedulerJob].getName} 的子类。"
      logger.error(msg)
      db.runTransaction(JobRepo.completionJobLog(logId, OffsetDateTime.now(), RunStatus.JOB_FAILURE, msg))
    }
  }
}
