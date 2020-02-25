package mass.job

import java.time.OffsetDateTime

import akka.actor.ExtendedActorSystem
import com.typesafe.scalalogging.StrictLogging
import fusion.common.extension.{ FusionExtension, FusionExtensionId }
import fusion.job.{ FusionJob, FusionScheduler }
import helloscala.common.exception.HSBadRequestException
import mass.core.job.{ JobConstants, SchedulerJob }
import mass.extension.MassSystem
import mass.model.job.{ JobItem, JobSchedule, JobTrigger, TriggerType }

import scala.collection.mutable
import scala.concurrent.ExecutionContext

object JobScheduler extends FusionExtensionId[JobScheduler] {
  override def createExtension(system: ExtendedActorSystem): JobScheduler = new JobScheduler(system)
}

final class JobScheduler private (override val classicSystem: ExtendedActorSystem)
    extends FusionExtension
    with StrictLogging {
  import org.quartz._

  val massSystem: MassSystem = MassSystem(classicSystem)

  val jobSettings: JobSettings = JobSettings(massSystem.core.settings)

  private val scheduler: FusionScheduler = FusionJob(classicSystem).component

  /**
   * 事件触发待执行Job队列。当事件发生时，执行任务
   * TODO 事件需要持久化
   */
  private val eventTriggerJobs = mutable.Map[String, Set[String]]()

  def name: String = classicSystem.name

  implicit def executionContext: ExecutionContext = classicSystem.dispatcher

//  /**
//   * 直接执行作业
//   * @param key 作业KEY
//   * @param jobItem 作业配置
//   * @param className 用于执行作业的Job类全限定名
//   * @return 作业开始执行时间
//   */
//  def executeJob(key: String, jobItem: JobItem, className: String): OffsetDateTime = {
//    val detail = buildJobDetail(key, jobItem, className, None)
//    val trigger = TriggerBuilder.newTrigger().startNow().build()
//    scheduler.scheduleJob(detail, trigger).atOffset(TimeUtils.DEFAULT_OFFSET)
//  }

  def triggerJob(key: String): OffsetDateTime = {
    scheduler.triggerJob(JobKey.jobKey(key))
    OffsetDateTime.now()
  }

  def scheduleJob(schedule: JobSchedule, jobClassName: String): OffsetDateTime = {
    scheduleJob(schedule.key, schedule.toJobItem, schedule.toJobTrigger, jobClassName, None)
  }

  /**
   * 将作业加入调度队列
   * @param key 作业KEY
   * @param jobItem 作业配置
   * @param jobTrigger 作业触发策略
   * @param jobClassName 用于执行作业的Job类全限定名
   * @param data 附加的作业数据
   * @param replace 是否覆盖已存在的作业
   * @return 作业加入调度队列时间
   */
  def scheduleJob(
      key: String,
      jobItem: JobItem,
      jobTrigger: JobTrigger,
      jobClassName: String,
      data: Option[Map[String, String]],
      replace: Boolean = true): OffsetDateTime =
    jobTrigger.triggerType match {
      case TriggerType.EVENT =>
        handleTriggerEventJob(key, jobTrigger)
      case _ =>
        val jobDetail = Option(scheduler.getJobDetail(JobKey.jobKey(key))) getOrElse
          buildJobDetail(key, jobItem, jobClassName, data)
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
  private def schedulerJob(jobDetail: JobDetail, trigger: Trigger, replace: Boolean): OffsetDateTime = {
    scheduler.scheduleJob(jobDetail, Set(trigger), replace)
    logger.info(s"Start job：${jobDetail.getKey}:${trigger.getKey}, $replace.")
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
        val ssb = SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(conf.interval.toMillis)
        if (conf.repeat > 0) ssb.withRepeatCount(conf.repeat) else ssb.repeatForever()
      case TriggerType.CRON  => CronScheduleBuilder.cronSchedule(conf.cronExpress)
      case TriggerType.EVENT => throw HSBadRequestException("事件触发不需要构建Trigger")
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
    JobBuilder.newJob(classOf[JobClassJob]).withIdentity(JobKey.jobKey(key)).setJobData(dataMap).storeDurably().build()
  }

  override def toString: String = s"JobSystem($name, $classicSystem)"
}
