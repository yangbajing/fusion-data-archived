package mass.model.job

import java.time.OffsetDateTime

import fusion.json.CborSerializable
import mass.message.job.{ JobCreateReq, JobUpdateReq }
import mass.model.CommonStatus

import scala.concurrent.duration.FiniteDuration

case class JobSchedule(
    key: String,
    program: Program,
    programOptions: Seq[String],
    programMain: String,
    programArgs: Seq[String],
    programVersion: String,
    resources: Map[String, String],
    data: Map[String, String],
    description: Option[String],
    dependentJobKeys: Seq[String],
    name: Option[String],
    triggerType: TriggerType,
    triggerEvent: String,
    startTime: OffsetDateTime,
    endTime: Option[OffsetDateTime],
    // 重复次数
    repeat: Int,
    // 每次重复间隔
    interval: FiniteDuration,
    cronExpress: String,
    failedRetries: Int,
    timeout: FiniteDuration,
    alarmEmails: Seq[String],
    status: CommonStatus = CommonStatus.DISABLE,
    creator: String = "",
    createdAt: OffsetDateTime = OffsetDateTime.now(),
    // 已执行次数
    scheduleCount: Long = 1)
    extends CborSerializable {
  if (endTime.isDefined) {
    require(
      endTime.isDefined && endTime.get.isAfter(startTime),
      s"The endTime cannot be earlier than startTime, ($startTime, ${endTime.get}).")
  }

  def toJobTrigger: JobTrigger =
    JobTrigger(
      triggerType,
      triggerEvent,
      Some(startTime),
      endTime,
      repeat,
      interval,
      cronExpress,
      failedRetries,
      timeout,
      alarmEmails)

  def toJobItem: JobItem =
    JobItem(
      program,
      programOptions,
      programMain,
      programArgs,
      programVersion,
      resources,
      data,
      description,
      dependentJobKeys,
      name)

  def mergeUpdate(req: JobUpdateReq): JobSchedule =
    copy(
      program = req.program.getOrElse(program),
      programOptions = req.programOptions.getOrElse(programOptions),
      programMain = req.programMain.getOrElse(programMain),
      programArgs = req.programArgs.getOrElse(programArgs),
      programVersion = req.programVersion.getOrElse(programVersion),
      resources = req.resources.getOrElse(resources),
      data = req.data.getOrElse(data),
      description = req.description.orElse(description),
      dependentJobKeys = req.dependentJobKeys.getOrElse(dependentJobKeys),
      name = req.name.orElse(name),
      triggerType = req.triggerType.getOrElse(triggerType),
      triggerEvent = req.triggerEvent.getOrElse(triggerEvent),
      startTime = req.startTime.getOrElse(startTime),
      endTime = req.endTime.orElse(endTime),
      repeat = req.repeat.getOrElse(repeat),
      interval = req.interval.getOrElse(interval),
      cronExpress = req.cronExpress.getOrElse(cronExpress),
      failedRetries = req.failedRetries.getOrElse(failedRetries),
      timeout = req.timeout.getOrElse(timeout),
      alarmEmails = req.alarmEmails.getOrElse(alarmEmails),
      status = req.status.getOrElse(status))
}

object JobSchedule {
  def fromJobItemAndTrigger(key: String, item: JobItem, trigger: JobTrigger): JobSchedule = {
    ???
  }
  def fromReq(key: String, req: JobCreateReq): JobSchedule =
    JobSchedule(
      key,
      req.item.program,
      req.item.programOptions,
      req.item.programMain,
      req.item.programArgs,
      req.item.programVersion,
      req.item.resources,
      req.item.data,
      req.item.description,
      req.item.dependentJobKeys,
      req.item.name,
      req.trigger.triggerType,
      req.trigger.triggerEvent,
      req.trigger.startTime.getOrElse(OffsetDateTime.now()),
      req.trigger.endTime,
      req.trigger.repeat,
      req.trigger.interval,
      req.trigger.cronExpress,
      req.trigger.failedRetries,
      req.trigger.timeout,
      req.trigger.alarmEmails)
}
