package mass.job

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.util.StringUtils
import mass.Global
import org.quartz.{ Job, JobExecutionContext }

import scala.collection.JavaConverters._

private[job] class JobClassJob extends Job with StrictLogging {
  override def execute(context: JobExecutionContext): Unit =
    try {
      val jobClass = context.getJobDetail.getJobDataMap.getString(JobConstants.JOB_CLASS)
      require(StringUtils.isNoneBlank(jobClass), s"Key: ${JobConstants.JOB_CLASS} 不能为空。")
      val data =
        context.getJobDetail.getJobDataMap.asScala.filterNot(_._1 == JobConstants.JOB_CLASS).mapValues(_.toString).toMap
      val jobSystem = JobSystem(Global.system)
      JobRunner.execute(jobSystem, context.getJobDetail.getKey.getName, data, jobClass)
    } catch {
      case e: Throwable =>
        logger.error(s"作业执行失败。${e.getMessage}", e)
    }
}
