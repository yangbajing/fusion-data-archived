package mass.scheduler

import java.nio.file.Files
import java.time.OffsetDateTime
import java.util.Properties

import akka.actor.ActorSystem
import com.typesafe.scalalogging.LazyLogging
import helloscala.common.Configuration
import helloscala.common.util.TimeUtils
import mass.core.job._
import mass.server.MassSystemExtension

import scala.concurrent.ExecutionContext

object SchedulerSystem {
  private var _instance: SchedulerSystem = _

  def instance: SchedulerSystem = _instance

  def apply(massSystem: MassSystemExtension): SchedulerSystem =
    apply(massSystem.name, massSystem, true)

  def apply(name: String,
            massSystem: MassSystemExtension,
            waitForJobsToComplete: Boolean): SchedulerSystem = {
    _instance = new SchedulerSystem(name, massSystem, waitForJobsToComplete)
    _instance
  }

}

class SchedulerSystem private (
    val name: String,
    val massSystem: MassSystemExtension,
    val waitForJobsToComplete: Boolean
) extends SchedulerSystemRef
    with LazyLogging {

  import org.quartz._
  import org.quartz.impl.StdSchedulerFactory

  private val props =
    configuration.get[Properties]("mass.core.scheduler.properties")
  private val scheduler: Scheduler = new StdSchedulerFactory(props).getScheduler
  val conf = new SchedulerConfig(configuration)
  init()

  private def init(): Unit = {
    props.forEach((key, value) => logger.info(s"[props] $key = $value"))

    if (!Files.isDirectory(conf.jobSavedPath)) {
      Files.createDirectories(conf.jobSavedPath)
    }

    scheduler.start()
    massSystem.system.registerOnTermination {
      scheduler.shutdown(waitForJobsToComplete)
    }
  }

  // TODO 定义 SchedulerSystem 自有的线程执行器
  override implicit def dispatcher: ExecutionContext =
    massSystem.system.dispatcher

  override def system: ActorSystem = massSystem.system

  override def configuration: Configuration = massSystem.configuration

  def schedulerJob[T <: SchedulerJob](
      conf: JobConf,
      jobClass: Class[T],
      data: Map[String, String]): OffsetDateTime =
    schedulerJob(conf, jobClass.getName, data)

  def schedulerJob[T <: SchedulerJob](
      conf: JobConf,
      className: String,
      data: Map[String, String]): OffsetDateTime = {
    val jobDetail = buildJobDetail(conf, className, data)
    val trigger = buildTrigger(conf)
    schedulerJob(jobDetail, trigger)
  }

  private def schedulerJob(detail: JobDetail,
                           trigger: Trigger): OffsetDateTime = {
    scheduler
      .scheduleJob(detail, trigger)
      .toInstant
      .atOffset(TimeUtils.ZONE_CHINA_OFFSET)
  }

  private def buildTrigger(conf: JobConf): Trigger = {
    var builder: TriggerBuilder[Trigger] = TriggerBuilder
      .newTrigger()
      .withIdentity(TriggerKey.triggerKey(conf.triggerKey))

    conf.startTime.foreach(st =>
      builder = builder.startAt(java.util.Date.from(st.toInstant)))
    conf.endTime.foreach(et =>
      builder = builder.endAt(java.util.Date.from(et.toInstant)))

    val schedule = conf match {
      case JobDurationConf(_, _, duration, repeat, _, _) =>
        val ssb = SimpleScheduleBuilder
          .simpleSchedule()
          .withIntervalInMilliseconds(duration.toMillis)
        if (repeat > 0) ssb.withRepeatCount(repeat) else ssb.repeatForever()

      case JobCronConf(_, _, express, _, _) =>
        CronScheduleBuilder.cronSchedule(express)
    }
    builder.withSchedule(schedule).build()
  }

  private def buildJobDetail(conf: JobConf,
                             className: String,
                             data: Map[String, String]): JobDetail = {
    val dataMap = new JobDataMap()
    dataMap.put(SchedulerUtils.JOB_CLASS, className)
    for ((key, value) <- data) {
      dataMap.put(key, value)
    }
    JobBuilder
      .newJob(classOf[JobClassJob])
      .withIdentity(JobKey.jobKey(conf.jobKey))
      .setJobData(dataMap)
      .build()
  }

  override def toString: String =
    s"SchedulerSystem($name, $massSystem, $waitForJobsToComplete)"
}
