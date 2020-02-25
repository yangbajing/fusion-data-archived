package mass.job

import com.typesafe.scalalogging.StrictLogging
import fusion.core.FusionApplication
import helloscala.common.util.StringUtils
import mass.core.job.JobConstants
import org.quartz.{ Job, JobExecutionContext }

import scala.jdk.CollectionConverters._

private[job] class JobClassJob extends Job with StrictLogging {
  override def execute(context: JobExecutionContext): Unit =
    try {
      val jobClass = context.getJobDetail.getJobDataMap.getString(JobConstants.JOB_CLASS)
      require(StringUtils.isNoneBlank(jobClass), s"Key: ${JobConstants.JOB_CLASS} 不能为空。")
      val data =
        context.getJobDetail.getJobDataMap.asScala.filterNot(_._1 == JobConstants.JOB_CLASS).mapValues(_.toString).toMap
      val jobSystem = JobScheduler(FusionApplication.application.classicSystem)
      JobRunner.execute(jobSystem, context.getJobDetail.getKey.getName, data, jobClass)
    } catch {
      case e: Throwable =>
        logger.error(s"作业执行失败。${e.getMessage}", e)
    }
}
