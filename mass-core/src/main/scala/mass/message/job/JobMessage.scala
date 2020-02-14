package mass.message.job

import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path
import java.time.OffsetDateTime

import akka.http.scaladsl.server.directives.FileInfo
import fusion.json.CborSerializable
import helloscala.common.data.{ IntValueName, StringValueName, ValueName }
import mass.common.page.{ Page, PageResult }
import mass.core.job.JobResult
import mass.model.CommonStatus
import mass.model.job.{ JobItem, JobSchedule, JobTrigger, Program, TriggerType }

import scala.concurrent.duration.FiniteDuration

sealed trait JobMessage extends CborSerializable
sealed trait JobResponse extends CborSerializable

case class ProgramVersionItem(programId: String, versions: Seq[StringValueName])
case class JobGetAllOptionReq() extends JobMessage
case class JobGetAllOptionResp(
    program: Seq[StringValueName],
    triggerType: Seq[ValueName[String]],
    programVersion: Seq[ProgramVersionItem],
    jobStatus: Seq[IntValueName])
    extends JobResponse

case class JobTriggerReq(key: String) extends JobMessage

case class JobCreateReq(key: Option[String], item: JobItem, trigger: JobTrigger) extends JobMessage
case class JobCreateResp(schedule: Option[JobSchedule]) extends JobResponse

case class JobUpdateReq(
    key: String,
    program: Option[Program] = None,
    programOptions: Option[Seq[String]] = None,
    programMain: Option[String] = None,
    programArgs: Option[Seq[String]] = None,
    programVersion: Option[String] = None,
    resources: Option[Map[String, String]] = None,
    data: Option[Map[String, String]] = None,
    description: Option[String] = None,
    dependentJobKeys: Option[Seq[String]] = None,
    name: Option[String] = None,
    triggerType: Option[TriggerType] = None,
    triggerEvent: Option[String] = None,
    startTime: Option[OffsetDateTime] = None,
    endTime: Option[OffsetDateTime] = None,
    // 重复次数
    repeat: Option[Int] = None,
    // 每次重复间隔
    interval: Option[FiniteDuration] = None,
    cronExpress: Option[String] = None,
    failedRetries: Option[Int] = None,
    timeout: Option[FiniteDuration] = None,
    alarmEmails: Option[Seq[String]] = None,
    status: Option[CommonStatus] = None)
    extends JobMessage

case class JobFindReq(key: String) extends JobMessage
case class JobSchedulerResp(schedule: Option[JobSchedule]) extends JobResponse

case class JobPageReq(page: Int = 1, size: Int = 20, key: Option[String] = None) extends Page with JobMessage
case class JobPageResp(content: Seq[JobSchedule], totalElements: Long, page: Int, size: Int)
    extends PageResult[JobSchedule]
    with JobResponse

case class JobListReq(key: String) extends JobMessage
case class JobListResp(items: Seq[JobSchedule]) extends JobResponse

case class SchedulerJobResult(
    start: OffsetDateTime,
    end: Option[OffsetDateTime],
    exitValue: Int,
    outPath: String,
    errPath: String)
    extends JobResult

case class JobUploadJobReq(file: Path, fileName: String, charset: Charset) extends JobMessage
case class JobUploadJobResp(resps: Seq[JobCreateResp]) extends JobResponse

case class JobUploadFilesReq(items: Seq[(FileInfo, File)]) extends JobMessage
case class JobUploadFilesResp(resources: Seq[IntValueName]) extends JobResponse
