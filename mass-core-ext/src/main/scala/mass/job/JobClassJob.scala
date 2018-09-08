package mass.job

import java.time.OffsetDateTime

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.exception.HSNotFoundException
import helloscala.common.types.ObjectId
import helloscala.common.util.StringUtils
import mass.Global
import mass.core.job.{SchedulerContext, SchedulerJob}
import mass.job.repository.JobRepo
import mass.model.job.{JobLog, RunStatus}
import org.quartz.{Job, JobExecutionContext}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

private[job] class JobClassJob extends Job with StrictLogging {

  override def execute(context: JobExecutionContext): Unit =
    try {
      val jobClass = context.getJobDetail.getJobDataMap.getString(JobConstants.JOB_CLASS)
      require(StringUtils.isNoneBlank(jobClass), s"Key: ${JobConstants.JOB_CLASS} 不能为空。")
      _execute(context, jobClass)
    } catch {
      case e: Throwable =>
        logger.error(s"作业执行失败。${e.getMessage}", e)
    }

  private def _execute(context: JobExecutionContext, jobClass: String) {
    val system = Global.system
    val jobSystem = JobSystem(system)
    val db = jobSystem.massSystem.sqlManager
    val key = context.getJobDetail.getKey.getName
    val triggerKey = context.getTrigger.getKey.getName
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
            val data = jobItem.data ++ (context.getJobDetail.getJobDataMap.asScala
              .mapValues(_.toString) - JobConstants.JOB_CLASS)
            val ctx = SchedulerContext(key, jobItem, triggerKey, data, system)
            val job = clz.newInstance().asInstanceOf[SchedulerJob]
            // TODO 超时控制、失败重试、失败告警
            job.run(ctx)
          case _ =>
            Future.failed(HSNotFoundException(s"作业：$key，触发器：$triggerKey 未找到"))
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
