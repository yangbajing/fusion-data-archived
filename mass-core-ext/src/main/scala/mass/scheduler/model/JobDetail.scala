package mass.scheduler.model

import java.io.File
import java.nio.charset.Charset
import java.time.OffsetDateTime

import com.fasterxml.jackson.databind.JsonNode
import helloscala.common.page.{Page, PageResult}
import helloscala.common.types.ObjectId
import massmsg.CommonStatus

import scala.concurrent.duration.FiniteDuration

trait JobMessage

case class JobPageReq(
    page: Int,
    size: Int
) extends Page
    with JobMessage
case class JobPageResp(
    content: Seq[JobDetail] = Nil,
    totalElements: Long = 0,
    page: Int = Page.DEFAULT_PAGE,
    size: Int = Page.DEFAULT_SIZE
) extends PageResult[JobDetail]
    with JobMessage

case class JobUploadJobReq(file: File, fileName: String, charset: Charset) extends JobMessage

case class JobDetail(key: String, data: Map[String, String], conf: Option[JsonNode], createdAt: OffsetDateTime)

case class JobTrigger(
    key: String,
    cronExpress: Option[String],
    duration: Option[FiniteDuration],
    repeat: Option[Int],
    startTime: Option[OffsetDateTime],
    endTime: Option[OffsetDateTime],
    conf: Option[JsonNode],
    createdAt: OffsetDateTime)

case class JobSchedule(
    id: ObjectId,
    detailKey: String,
    triggerKey: String,
    status: CommonStatus,
    createdAt: OffsetDateTime)

case class JobLog(
    id: ObjectId,
    /**
     * FK [[JobSchedule.id]]
     */
    jobId: ObjectId,
    startTime: OffsetDateTime,
    completionTime: Option[OffsetDateTime],
    completionStatus: Option[Int],
    completionValue: Option[String],
    createdAt: OffsetDateTime)
