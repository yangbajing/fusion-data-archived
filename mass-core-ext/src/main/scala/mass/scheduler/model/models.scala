package mass.scheduler.model

import java.time.OffsetDateTime

import helloscala.common.types.ObjectId
import mass.core.json.EnumSerializer
import mass.scheduler.model.JobScheduleStatus.JobScheduleStatus
import org.json4s.JValue

import scala.concurrent.duration.FiniteDuration

case class JobDetail(key: String,
                     data: Map[String, String],
                     conf: Option[JValue],
                     createdAt: OffsetDateTime)

case class JobTrigger(key: String,
                      cronExpress: Option[String],
                      duration: Option[FiniteDuration],
                      repeat: Option[Int],
                      startTime: Option[OffsetDateTime],
                      endTime: Option[OffsetDateTime],
                      conf: Option[JValue],
                      createdAt: OffsetDateTime)

object JobScheduleStatus extends Enumeration {
  type JobScheduleStatus = Value
  val Disable = Value(0)
  val Enable = Value(1)

  implicit object enumSerializer extends EnumSerializer(JobScheduleStatus)

  //  implicit object EnumIdSerializer extends CustomSerializer[JobScheduleStatus](format => ( {
  //    case JInt(i) => JobScheduleStatus(i.intValue())
  //    case JNull => null
  //  }, {
  //    case x: JobScheduleStatus => JInt(x.id)
  //  }))

}

case class JobSchedule(id: ObjectId,
                       detailKey: String,
                       triggerKey: String,
                       status: JobScheduleStatus,
                       createdAt: OffsetDateTime)

case class JobLog(id: ObjectId,
                  /**
                    * FK [[JobSchedule.id]]
                    */
                  jobId: ObjectId,
                  startTime: OffsetDateTime,
                  completionTime: Option[OffsetDateTime],
                  completionStatus: Option[Int],
                  completionValue: Option[String],
                  createdAt: OffsetDateTime)
