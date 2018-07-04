package mass.core.job

import java.time.OffsetDateTime

import helloscala.common.util.StringUtils

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

trait JobConf {
  /**
   * 任务 key
   */
  val jobKey: String

  /**
   * 触发器 key
   */
  val triggerKey: String

  /**
   * 任务开始时间，未设置则马上开始
   */
  val startTime: Option[OffsetDateTime]

  /**
   * 任务结束时间，未设置则一直有效
   */
  val endTime: Option[OffsetDateTime]
}

object JobConf {

  def builder(jobKey: String, triggerKey: String): Builder = new Builder(jobKey, triggerKey)

  private[job] class Builder(jobKey: String, triggerKey: String) {
    var startTime: OffsetDateTime = _
    var endTime: OffsetDateTime = _
    var repeat: Int = -1
    var duration: FiniteDuration = _
    var cronExpress: String = _

    def withStartTime(startTime: OffsetDateTime): Builder = {
      this.startTime = startTime
      this
    }

    def withEndTime(endTime: OffsetDateTime): Builder = {
      this.endTime = endTime
      this
    }

    def withRepeat(repeat: Int): Builder = {
      this.repeat = repeat
      this
    }

    def withDuration(duration: FiniteDuration): Builder = {
      this.duration = duration
      this
    }

    def withCronExpress(express: String): Builder = {
      this.cronExpress = express
      this
    }

    def result: JobConf = {
      if (StringUtils.isNoneBlank(cronExpress))
        JobCronConf(jobKey, triggerKey, cronExpress, Option(startTime), Option(endTime))
      else if (duration != null && repeat >= 0)
        JobDurationConf(jobKey, triggerKey, duration, repeat, Option(startTime), Option(endTime))
      else throw new IllegalArgumentException("构建错误")
    }
  }

}

case class JobDurationConf(
    jobKey: String,
    triggerKey: String,
    duration: FiniteDuration,
    repeat: Int = 0,
    startTime: Option[OffsetDateTime] = None,
    endTime: Option[OffsetDateTime] = None) extends JobConf

case class JobCronConf(
    jobKey: String,
    triggerKey: String,
    cronExpress: String,
    startTime: Option[OffsetDateTime] = None,
    endTime: Option[OffsetDateTime] = None) extends JobConf

case class SchedulerContext(data: Map[String, String], scheduler: SchedulerSystemRef)

trait JobResult

trait SchedulerJob {

  def run(context: SchedulerContext): Future[JobResult]

}
