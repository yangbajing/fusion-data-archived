package mass.model.job

import java.time.OffsetDateTime

import fusion.json.CborSerializable
import helloscala.common.Configuration
import mass.core.job.JobConstants

import scala.concurrent.duration._

// #JobTrigger
case class JobTrigger(
    triggerType: TriggerType,
    triggerEvent: String = "",
    startTime: Option[OffsetDateTime] = None,
    endTime: Option[OffsetDateTime] = None,
    // 重复次数
    repeat: Int = JobConstants.TRIGGER_REPEAT,
    // 每次重复间隔
    interval: FiniteDuration = JobConstants.TRIGGER_INTERVAL,
    cronExpress: String = "",
    description: Option[String] = None,
    failedRetries: Int = 0,
    timeout: FiniteDuration = JobConstants.RUN_TIMEOUT,
    alarmEmails: Seq[String] = Nil)
    extends CborSerializable
// #JobTrigger

object JobTrigger {
  def apply(c: Configuration): JobTrigger = {
    val triggerType =
      TriggerType.optionalFromName(c.getString("trigger-type").toUpperCase()).orElse(TriggerType.TRIGGER_UNKNOWN)
    JobTrigger(
      triggerType,
      c.getOrElse[String]("trigger-event", ""),
      c.get[Option[OffsetDateTime]]("start-time"),
      c.get[Option[OffsetDateTime]]("end-time"),
      c.getOrElse[Int]("repeat", JobConstants.TRIGGER_REPEAT),
      c.getOrElse[FiniteDuration]("duration", JobConstants.TRIGGER_INTERVAL),
      c.getOrElse[String]("cron-express", ""),
      c.get[Option[String]]("description"),
      c.getOrElse[Int]("failed-retries", 0),
      c.getOrElse[FiniteDuration]("timeout", JobConstants.RUN_TIMEOUT),
      c.getOrElse[Seq[String]]("alarm-emails", Nil))
  }
}
