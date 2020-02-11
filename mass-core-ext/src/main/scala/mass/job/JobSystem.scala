package mass.job

import java.nio.file.Files
import java.time.OffsetDateTime
import java.util.Properties

import akka.actor.{ ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import com.typesafe.scalalogging.LazyLogging
import helloscala.common.Configuration
import helloscala.common.exception.HSBadRequestException
import helloscala.common.util.TimeUtils
import mass.core.Constants
import mass.core.job.{ SchedulerJob, SchedulerSystemRef }
import mass.extension.MassSystem
import mass.data.job.{ JobItem, JobTrigger, TriggerType }

import scala.collection.mutable
import scala.concurrent.ExecutionContext

object JobSystem extends ExtensionId[JobSystem] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): JobSystem = new JobSystem(system, true)
  override def lookup(): ExtensionId[_ <: Extension] = JobSystem
}

final class JobSystem private (val system: ExtendedActorSystem, val waitForJobsToComplete: Boolean)
    extends SchedulerSystemRef
    with Extension
    with LazyLogging {
  import org.quartz._
  import org.quartz.impl.StdSchedulerFactory

  val massSystem: MassSystem = MassSystem(system)

  val jobSettings = JobSettings(massSystem.settings)

  private val scheduler: org.quartz.Scheduler =
    new StdSchedulerFactory(configuration.get[Properties](s"${Constants.BASE_CONF}.job.scheduler.properties")).getScheduler

  /**
   * 事件触发待执行Job队列。当事件发生时，执行任务
   * TODO 事件需要持久化
   */
  private val eventTriggerJobs = mutable.Map[String, Set[String]]()
  init()

  private def init(): Unit = {
    if (!Files.isDirectory(jobSettings.jobSavedDir)) {
      Files.createDirectories(jobSettings.jobSavedDir)
    }

    scheduler.start()
    system.registerOnTermination {
      scheduler.shutdown(waitForJobsToComplete)
    }
  }

  def name: String = system.name

  def configuration: Configuration = massSystem.connection

  // TODO 定义 SchedulerSystem 自有的线程执行器
  implicit override def executionContext: ExecutionContext = system.dispatcher

  /**
   * 直接执行作业
   * @param key 作业KEY
   * @param jobItem 作业配置
   * @param className 用于执行作业的Job类全限定名
   * @return 作业开始执行时间
   */
  def executionJob(key: String, jobItem: JobItem, className: String): OffsetDateTime = {
    val detail = buildJobDetail(key, jobItem, className, None)
    val trigger = TriggerBuilder.newTrigger().startNow().build()
    scheduler.scheduleJob(detail, trigger).toInstant.atOffset(TimeUtils.ZONE_CHINA_OFFSET)
  }

  def triggerJob(key: String): OffsetDateTime = {
    scheduler.triggerJob(JobKey.jobKey(key))
    OffsetDateTime.now()
  }

  /**
   * 将作业加入调度队列
   * @param key 作业KEY
   * @param jobItem 作业配置
   * @param jobTrigger 作业触发策略
   * @param className 用于执行作业的Job类全限定名
   * @param data 附加的作业数据
   * @param replace 是否覆盖已存在的作业
   * @return 作业加入调度队列时间
   */
  def scheduleJob(
      key: String,
      jobItem: JobItem,
      jobTrigger: JobTrigger,
      className: String,
      data: Option[Map[String, String]],
      replace: Boolean = true): OffsetDateTime =
    jobTrigger.triggerType match {
      case TriggerType.EVENT =>
        handleTriggerEventJob(key, jobTrigger)
      case _ =>
        val jobDetail = Option(scheduler.getJobDetail(JobKey.jobKey(key))) getOrElse
          buildJobDetail(key, jobItem, className, data)
        val trigger = Option(scheduler.getTrigger(TriggerKey.triggerKey(key))) getOrElse
          buildTrigger(key, jobTrigger)
        schedulerJob(jobDetail, trigger, replace)
    }

  /**
   * 将作业加入调度列表
   * @param jobDetail 作业明细
   * @param trigger 触发
   * @param replace 是否覆盖已存在作业
   * @return 作业加入调度队列时间
   */
  def schedulerJob(jobDetail: JobDetail, trigger: Trigger, replace: Boolean): OffsetDateTime = {
    scheduler.scheduleJob(jobDetail, java.util.Collections.singleton(trigger), replace)
    logger.info(s"启动作业：${jobDetail.getKey}:${trigger.getKey}, $replace")
    OffsetDateTime.now()
  }

  /**
   *
   * @param key
   * @param jobTrigger
   * @param jobItems
   * @param className
   * @return 作业被重置调度队列时间
   */
  def rescheduleJob(key: String, jobTrigger: JobTrigger, jobItems: Seq[JobItem], className: String): OffsetDateTime = {
    import scala.collection.JavaConverters._
    scheduler.deleteJobs(jobItems.map(item => JobKey.jobKey(key)).asJava)
    for (item <- jobItems) {
      val trigger = buildTrigger(key, jobTrigger, jobKey = Some(key))
      scheduler.scheduleJob(buildJobDetail(key, item, className, Some(item.data)), trigger)
    }
    OffsetDateTime.now()
  }

  private def handleTriggerEventJob(key: String, triggerConf: JobTrigger): OffsetDateTime = {
    // 将事件触发Job加入队列
    val values = eventTriggerJobs.get(triggerConf.triggerEvent) match {
      case Some(list) => list + key
      case _          => Set(key)
    }
    eventTriggerJobs.put(triggerConf.triggerEvent, values)
    OffsetDateTime.now()
  }

  private def buildTrigger(key: String, conf: JobTrigger, jobKey: Option[String] = None): Trigger = {
    var builder: TriggerBuilder[Trigger] =
      TriggerBuilder.newTrigger().withIdentity(TriggerKey.triggerKey(key))

    conf.startTime.foreach(st => builder = builder.startAt(java.util.Date.from(st.toInstant)))
    conf.endTime.foreach(et => builder = builder.endAt(java.util.Date.from(et.toInstant)))
    jobKey.foreach(key => builder = builder.forJob(key))

    val schedule = conf.triggerType match {
      case TriggerType.SIMPLE =>
        val ssb = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(conf.duration.toMillis)
        if (conf.repeat > 0) ssb.withRepeatCount(conf.repeat) else ssb.repeatForever()
      case TriggerType.CRON  => CronScheduleBuilder.cronSchedule(conf.cronExpress)
      case TriggerType.EVENT => throw HSBadRequestException("事件触发不需要构建Trigger")
      case other             => throw HSBadRequestException(s"无效的触发器类型：$other")
    }
    builder.withSchedule(schedule).build()
  }

  private def buildJobDetail(
      key: String,
      item: JobItem,
      className: String,
      data: Option[Map[String, String]]): JobDetail = {
    require(
      classOf[SchedulerJob].isAssignableFrom(Class.forName(className)),
      s"className 必需为 ${classOf[SchedulerJob].getName} 的子类")
    val dataMap = new JobDataMap()
    dataMap.put(JobConstants.JOB_CLASS, className)
    for ((key, value) <- data.getOrElse(item.data)) {
      dataMap.put(key, value)
    }
    JobBuilder.newJob(classOf[JobClassJob]).withIdentity(JobKey.jobKey(key)).setJobData(dataMap).build()
  }

  override def toString: String = s"JobSystem($name, $system, $waitForJobsToComplete)"
}
