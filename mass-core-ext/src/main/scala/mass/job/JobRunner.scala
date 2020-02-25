package mass.job

import java.time.OffsetDateTime

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.types.ObjectId
import helloscala.common.util.StringUtils
import mass.core.job.{ SchedulerContext, SchedulerJob }
import mass.job.db.model.QrtzModels
import mass.job.repository.JobRepo
import mass.model.job.RunStatus

import scala.concurrent.ExecutionContext
import scala.util.{ Failure, Success }

object JobRunner extends StrictLogging {
  def execute(jobSystem: JobScheduler, key: String, extData: Map[String, String], jobClass: String): Unit = {
    implicit val ec: ExecutionContext = jobSystem.executionContext
    val db = jobSystem.massSystem.sqlSystem
    val logId = ObjectId.getString()
    val createdAt = OffsetDateTime.now()

    val log =
      QrtzModels.QrtzTriggerLog(
        logId,
        key,
        "DEFAULT",
        key,
        "DEFAULT",
        createdAt,
        None,
        RunStatus.JOB_RUNNING,
        None,
        createdAt)
    logger.info(s"Job start execution，log id is '$logId'.")
    db.runTransaction(JobRepo.insertJobLog(log))

    val clz = Class.forName(jobClass)
    if (classOf[SchedulerJob].isAssignableFrom(clz)) {
//      db.run(JobRepo.findJob(key))
//        .flatMap {
//          case Some(schedule) =>
//            val data = schedule.data ++ extData
//            val ctx = SchedulerContext(key, schedule.toJobItem, data, jobSystem.system)
//            val job = jobSystem.system.dynamicAccess.createInstanceFor[SchedulerJob](clz, Nil).get
//            // TODO 超时控制、失败重试、失败告警
//            job.run(ctx)
//          case _ =>
//            Future.failed(HSNotFoundException(s"Job key not found, it is '$key'."))
//        }

      val ctx = SchedulerContext(key, null, extData, jobSystem.typedSystem)
      val job = jobSystem.classicSystem.dynamicAccess.createInstanceFor[SchedulerJob](clz, Nil).get
      // TODO 超时控制、失败重试、失败告警
      job.run(ctx).onComplete { v =>
        val completionTime = OffsetDateTime.now()
        v match {
          case Success(msg) =>
            logger.info(s"Job executed successfully，log id is '$logId'.")
            db.runTransaction(JobRepo.completionJobLog(logId, completionTime, RunStatus.JOB_OK, msg.toString))
          case Failure(e) =>
            logger.error(s"Job execution failed，log id is '$logId'. The exception thrown is [${e.toString}].")
            db.runTransaction(
              JobRepo.completionJobLog(logId, completionTime, RunStatus.JOB_FAILURE, StringUtils.toString(e)))
        }
      }
    } else {
      val msg = s"Unknown job type: '$jobClass', subtype of '${classOf[SchedulerJob].getName}' required."
      logger.error(msg)
      db.runTransaction(JobRepo.completionJobLog(logId, OffsetDateTime.now(), RunStatus.JOB_FAILURE, msg))
    }
  }
}
