package mass.scheduler

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.util.StringUtils
import mass.core.job.{SchedulerContext, SchedulerJob, SchedulerUtils}
import org.quartz.{Job, JobExecutionContext}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}

private[mass] class JobClassJob extends Job with StrictLogging {

  override def execute(context: JobExecutionContext): Unit = {
    val jobClass =
      context.getJobDetail.getJobDataMap.getString(SchedulerUtils.JOB_CLASS)
    require(StringUtils.isNoneBlank(jobClass), s"JOB_CLASS 不能为空。")

    Class.forName(jobClass).newInstance() match {
      case job: SchedulerJob =>
        val data = (context.getJobDetail.getJobDataMap.asScala
          .mapValues(_.toString) - SchedulerUtils.JOB_CLASS).toMap
        val ctx = SchedulerContext(data, SchedulerSystem.instance)

        job
          .run(ctx)
          .onComplete {
            case Success(result) =>
              logger.info(s"调度任务执行成功：$result。")
            case Failure(e) =>
              logger.error(s"调度任务执行错误：$jobClass。", e)
          }(ctx.scheduler.dispatcher)

      case unknown =>
        logger.error(
          s"未知的任务类型：${unknown.getClass.getName}，需要 ${classOf[SchedulerJob].getName} 的子类。")
    }
  }

}
