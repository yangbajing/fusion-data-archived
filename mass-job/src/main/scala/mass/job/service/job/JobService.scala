package mass.job.service.job

import java.nio.file.{ Files, Paths, StandardCopyOption }
import java.time.OffsetDateTime

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.data.{ IntValueName, StringValueName }
import helloscala.common.util.DigestUtils
import mass.job.JobSystem
import mass.job.component.DefaultSchedulerJob
import mass.job.repository.JobRepo
import mass.job.util.{ JobUtils, ProgramVersion }
import mass.message.job._
import mass.model.job._
import mass.slick.SqlManager

import scala.concurrent.{ ExecutionContext, Future }

trait JobService extends StrictLogging {
  val jobSystem: JobSystem

  protected val JOB_CLASS_NAME: String = classOf[DefaultSchedulerJob].getName
  protected lazy val db: SqlManager = jobSystem.massSystem.sqlManager

  def executionJob(event: JobExecutionEvent)(implicit ec: ExecutionContext): Unit = {
    db.run(JobRepo.findJob(event.key)).foreach {
      case Some(schedule) => jobSystem.triggerJob(schedule.key)
      case None           => logger.error(s"作业未找到，jobKey: ${event.key}")
    }
  }

  def handleList(req: JobListReq)(implicit ec: ExecutionContext): Future[JobListResp] = {
    db.run(JobRepo.listJob(req)).map(list => JobListResp(list))
  }

  def handlePage(req: JobPageReq)(implicit ec: ExecutionContext): Future[JobPageResp] = db.run(JobRepo.page(req))

  def handleFind(req: JobFindReq)(implicit ec: ExecutionContext): Future[JobSchedulerResp] = {
    db.run(JobRepo.findJob(req.key)).map(maybe => JobSchedulerResp(maybe))
  }

  def handleUploadJob(req: JobUploadJobReq)(implicit ec: ExecutionContext): Future[JobUploadJobResp] =
    JobUtils
      .uploadJob(jobSystem.jobSettings, req)
      .flatMap(jobZip => db.runTransaction(JobRepo.save(jobZip)(db.executionContext)))
      .map { list =>
        val results = list.map(schedule => JobCreateResp(Option(schedule)))
        JobUploadJobResp(results)
      }

  def handleGetAllOption(req: JobGetAllOptionReq)(implicit ec: ExecutionContext): Future[JobGetAllOptionResp] = Future {
    val programs = Program.values().toList.map(_.toValueName)
    val triggerType = TriggerType.values().toList.map(_.toValueName)
    val programVersion = ProgramVersion.values
      .groupBy(_.NAME)
      .map {
        case (program, versions) =>
          ProgramVersionItem(program.getValue, versions.map(p => StringValueName(p.VERSION, p.VERSION)))
      }
      .toList
    val jobStatus = RunStatus.values().toList.map(_.toValueName)
    JobGetAllOptionResp(programs, triggerType, programVersion, jobStatus)
  }

  def handleCreateJob(req: JobCreateReq)(implicit ec: ExecutionContext): Future[JobCreateResp] = {
    db.runTransaction(JobRepo.save(req)).map { schedule =>
      JobCreateResp(Option(schedule))
    }
  }

  def handleUpdate(req: JobUpdateReq)(implicit ec: ExecutionContext): Future[JobSchedulerResp] = {
    db.runTransaction(JobRepo.updateJobSchedule(req)).map { schedule =>
      schedulerJob(schedule)
      JobSchedulerResp(Option(schedule))
    }
  }

  def handleUploadFiles(req: JobUploadFilesReq)(implicit ec: ExecutionContext): Future[JobUploadFilesResp] = Future {
    val jobSettings = jobSystem.jobSettings
    val resources = req.items.zipWithIndex.map {
      case ((fileInfo, file), idx) =>
        val sha256 = DigestUtils.sha256HexFromPath(file.toPath)
        val relativePath = Paths.get(sha256.take(2), sha256, fileInfo.fileName)
        val dist = jobSettings.jobSavedDir.resolve(relativePath)
        Files.move(file.toPath, dist, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        IntValueName(idx, relativePath.toString)
    }
    JobUploadFilesResp(resources)
  }

  private def schedulerJob(schedule: JobSchedule): OffsetDateTime = {
    jobSystem.scheduleJob(schedule.key, schedule.item, schedule.trigger, JOB_CLASS_NAME, None)
  }
}
