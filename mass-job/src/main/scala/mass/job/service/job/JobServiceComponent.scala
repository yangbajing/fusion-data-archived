package mass.job.service.job

import java.nio.file.{ Files, Paths, StandardCopyOption }

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.data.{ IntValueName, StringValueName }
import helloscala.common.util.DigestUtils
import mass.core.ProgramVersion
import mass.db.slick.SqlSystem
import mass.job.JobScheduler
import mass.job.component.DefaultSchedulerJob
import mass.job.repository.JobRepo
import mass.job.util.JobUtils
import mass.message.job._
import mass.model.CommonStatus
import mass.model.job._

import scala.concurrent.{ ExecutionContext, Future }

trait JobServiceComponent extends StrictLogging {
  val jobScheduler: JobScheduler

  protected val JOB_CLASS_NAME: String = classOf[DefaultSchedulerJob].getName
  private def db: SqlSystem = jobScheduler.massSystem.sqlManager

  def triggerJob(event: JobTriggerEvent)(implicit ec: ExecutionContext): Unit = {
    db.run(JobRepo.findJob(event.key)).foreach {
      case Some(schedule) => jobScheduler.triggerJob(schedule.key)
      case None           => logger.error(s"Job not found, job key is '${event.key}'.")
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
    JobUtils.uploadJob(jobScheduler.jobSettings, req).flatMap(jobZip => db.runTransaction(JobRepo.save(jobZip))).map {
      list =>
        val results = list.map(schedule => JobCreateResp(Option(schedule)))
        JobUploadJobResp(results)
    }

  def handleGetAllOption(req: JobGetAllOptionReq)(implicit ec: ExecutionContext): Future[JobGetAllOptionResp] = Future {
    val programs = Program.values.map(_.toValueName)
    val triggerType = TriggerType.values.toList.map(_.toValueName)
    val programVersion = ProgramVersion.values
      .groupBy(_.program)
      .map {
        case (program, versions) =>
          ProgramVersionItem(program.value, versions.map(p => StringValueName(p.version, p.version)))
      }
      .toList
    val jobStatus = RunStatus.values().toList.map(_.toValueName)
    JobGetAllOptionResp(programs, triggerType, programVersion, jobStatus)
  }

  def handleCreateJob(req: JobCreateReq)(implicit ec: ExecutionContext): Future[JobCreateResp] = {
    db.runTransaction(JobRepo.save(req)).map { schedule =>
      if (schedule.status == CommonStatus.ENABLE) {
        jobScheduler.scheduleJob(schedule, JOB_CLASS_NAME)
      }
      JobCreateResp(Option(schedule))
    }
  }

  def handleUpdate(req: JobUpdateReq)(implicit ec: ExecutionContext): Future[JobSchedulerResp] = {
    db.runTransaction(JobRepo.updateJobSchedule(req)).map { schedule =>
      JobSchedulerResp(Option(schedule))
    }
  }

  def handleUploadFiles(req: JobUploadFilesReq)(implicit ec: ExecutionContext): Future[JobUploadFilesResp] = Future {
    val jobSettings = jobScheduler.jobSettings
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

  def handleScheduleJob(req: JobScheduleReq)(implicit ec: ExecutionContext): Future[JobSchedulerResp] = {
    db.run(JobRepo.findJob(req.key)).map { maybe =>
      maybe match {
        case Some(schedule) => jobScheduler.scheduleJob(schedule, JOB_CLASS_NAME)
        case None           => logger.error(s"Job not found, job key is '${req.key}'.")
      }
      JobSchedulerResp(maybe)
    }
  }
}
