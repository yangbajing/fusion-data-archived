package mass.scheduler.business.service

import java.io.File
import java.nio.charset.Charset
import java.nio.file.{Files, Path}
import java.util.zip.{ZipEntry, ZipFile}

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import helloscala.common.exception.HSBadRequestException
import helloscala.common.util.{DigestUtils, FileUtils, Utils}
import mass.core.job.JobConf
import mass.scheduler.SchedulerConfig
import mass.scheduler.model.JobUploadJobReq

import scala.concurrent.{ExecutionContext, Future}

object JobService extends StrictLogging {

  val JOB_CONF = "job.conf"

  def uploadJob(conf: SchedulerConfig, req: JobUploadJobReq)(implicit ec: ExecutionContext): Future[JobZip] = Future {
    val jobZip = parseJobZip(req.file, req.charset) match {
      case Right(v) => v
      case Left(e)  => throw e
    }

    val dest = conf.jobSavedPath
      .resolve(jobZip.sha.take(2))
      .resolve(jobZip.sha)
    logger.debug(s"dest: $dest")

    val buf = Array.ofDim[Byte](1024)
    val savedEntries = jobZip.entries.map { entry =>
      val entryName = entry.getName
      val savePath = dest.resolve(entryName)
      if (!Files.isDirectory(savePath.getParent)) {
        Files.createDirectories(savePath.getParent)
      }
      FileUtils.write(jobZip.zip.getInputStream(entry), Files.newOutputStream(savePath), buf)
      savePath
    }

    Files.copy(req.file.toPath, dest.resolve(req.fileName))
    JobZip(jobZip.sha, JobConf.parseConfiguration(jobZip.conf), savedEntries)
  }

  def parseJobZip(file: File, charset: Charset): Either[Throwable, JobZipTO] =
    Utils.either {
      import scala.collection.JavaConverters._

      val zip = new ZipFile(file, charset)
      val fileSha = DigestUtils.sha256Hex(file.toPath)
      val (confEntries, entries) = zip
        .entries()
        .asScala
        .filterNot(entry => entry.isDirectory)
        .span(entry => entry.getName == JOB_CONF && !entry.isDirectory)
      confEntries.toList.headOption match {
        case Some(confEntry) =>
          val conf = parseJobConf(FileUtils.getString(zip.getInputStream(confEntry), charset))
          JobZipTO(zip, fileSha, conf, entries.toVector)
        case None =>
          throw HSBadRequestException("压缩包缺少 job.conf 配置文件")
      }
    }

  def parseJobConf(content: String): Configuration = {
    val conf = Configuration.parseString(content)
    require(conf.has("type"), "type 配置未设置，可选值：java, scala, javascript, shell, python")
    conf
  }

}

private[mass] case class JobZipTO(zip: ZipFile, sha: String, conf: Configuration, entries: Vector[ZipEntry])

case class JobZip(sha: String, conf: JobConf, entries: Vector[Path])
