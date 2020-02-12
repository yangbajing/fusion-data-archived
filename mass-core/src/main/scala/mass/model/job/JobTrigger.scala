package mass.model.job

import java.time.OffsetDateTime

import fusion.json.CborSerializable

import scala.concurrent.duration._

// #JobTrigger
case class JobTrigger(
    triggerType: TriggerType,
    triggerEvent: String = "",
    startTime: Option[OffsetDateTime] = None,
    endTime: Option[OffsetDateTime] = None,
    repeat: Int = 1,
    duration: FiniteDuration = 1.hour,
    cronExpress: String = "",
    description: Option[String] = None,
    failedRetries: Int = 0,
    timeout: FiniteDuration = 2.hours,
    alarmEmails: Seq[String] = Nil)
    extends CborSerializable
// #JobTrigger
