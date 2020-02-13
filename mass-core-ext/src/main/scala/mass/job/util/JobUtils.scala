package mass.job.util

import java.io.File
import java.nio.charset.Charset
import java.nio.file.{ Files, Path, StandardCopyOption }
import java.util.zip.ZipFile

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import helloscala.common.util.{ DigestUtils, Utils }
import mass.common.util.FileUtils
import mass.core.job.JobConstants
import mass.job.JobSettings
import mass.message.job._
import mass.model.job.{ JobItem, JobTrigger }

import scala.concurrent.{ ExecutionContext, Future }

/**
 *
 * TODO jobKey 存储目录时是否需要加个前缀？
 */
object JobUtils extends StrictLogging {
  case class JobZipInternal private (configs: Vector[JobCreateReq], entries: Vector[Path])

  def uploadJob(jobSettings: JobSettings, req: JobUploadJobReq)(implicit ec: ExecutionContext): Future[JobZip] =
    Future {
      val sha256 = DigestUtils.sha256HexFromPath(req.file)
      val dest = jobSettings.jobSavedDir.resolve(sha256.take(2)).resolve(sha256)

      val jobZipInternal = parseJobZip(req.file, req.charset, dest.resolve(JobConstants.DIST)) match {
        case Right(v) => v
        case Left(e)  => throw e
      }

      val zipPath = dest.resolve(req.fileName)
      Files.move(req.file, zipPath, StandardCopyOption.REPLACE_EXISTING)
      JobZip(zipPath, jobZipInternal.configs, jobZipInternal.entries)
    }

  @inline def parseJobZip(file: Path, charset: Charset, dest: Path): Either[Throwable, JobZipInternal] =
    parseJobZip(file.toFile, charset, dest)

  def parseJobZip(file: File, charset: Charset, dest: Path): Either[Throwable, JobZipInternal] = Utils.either {
    import scala.jdk.CollectionConverters._
    import scala.language.existentials

    val zip = new ZipFile(file, charset)
    try {
      val (confEntries, fileEntries) = zip
        .entries()
        .asScala
        .filterNot(entry => entry.isDirectory)
        .span(entry => entry.getName.endsWith(JobConstants.ENDS_SUFFIX) && !entry.isDirectory)
      val configs =
        confEntries.map(confEntry =>
          parseJobConf(FileUtils.getString(zip.getInputStream(confEntry), charset, "\n")) match {
            case Right(config) => config
            case Left(e)       => throw e
          })

      val buf = Array.ofDim[Byte](1024)
      val entryPaths = fileEntries.map { entry =>
        val entryName = entry.getName
        val savePath = dest.resolve(entryName)
        if (!Files.isDirectory(savePath.getParent)) {
          Files.createDirectories(savePath.getParent)
        }
        FileUtils.write(zip.getInputStream(entry), Files.newOutputStream(savePath), buf) // zip entry存磁盘
        savePath
      }

      JobZipInternal(configs.toVector, entryPaths.toVector)
    } finally {
      if (zip ne null) zip.close()
    }
  }

  def parseJobConf(content: String): Either[Throwable, JobCreateReq] = Utils.either {
    val conf = Configuration.parseString(content)
    val jobItem = JobItem(conf.getConfiguration("item"))
    val jobTrigger = JobTrigger(conf.getConfiguration("trigger"))
    JobCreateReq(conf.get[Option[String]]("key"), jobItem, jobTrigger, None)
  }
}

case class JobZip(zipPath: Path, configs: Vector[JobCreateReq], entries: Vector[Path])
