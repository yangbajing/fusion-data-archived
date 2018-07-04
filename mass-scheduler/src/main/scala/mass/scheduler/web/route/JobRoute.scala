package mass.scheduler.web.route

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.util.zip.ZipFile

import akka.stream.scaladsl.{FileIO, Source}
import helloscala.common.util.StringUtils
import mass.server.route.AbstractRoute

import scala.collection.JavaConverters._

class JobRoute extends AbstractRoute {

  def uploadJobRoute = pathPost("upload_job") {
    storeUploadedFile("job", createTempFileFunc) { case (fileInfo, file) =>
      val zip = new ZipFile(file, fileInfo.contentType.charsetOption.map(_.nioCharset()).getOrElse(StandardCharsets.UTF_8))
      val dest = Paths.get(StringUtils.option(fileInfo.fileName).getOrElse("/tmp/job-001"))

      for (entry <- zip.entries().asScala) {
        val entryName = entry.getName
        val savePath = dest.resolve(entryName)
        if (!Files.isDirectory(savePath.getParent)) {
          Files.createDirectories(savePath.getParent)
        }
        if (!entry.isDirectory) {
          val in = zip.getInputStream(entry)
          val out = Files.newOutputStream(savePath)
          val buf = Array.ofDim[Byte](1024)
          var len = in.read(buf)
          while (len > 0) {
            out.write(buf, 0, len)
            len = in.read(buf)
          }
          in.close()
          out.close()
        }
      }

      completeOk
    }
  }

}
