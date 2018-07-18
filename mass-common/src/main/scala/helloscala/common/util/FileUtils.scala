package helloscala.common.util

import java.io.{InputStream, OutputStream}
import java.nio.charset.Charset

import com.typesafe.scalalogging.StrictLogging

object FileUtils extends StrictLogging {

  def getString(in: InputStream, charset: Charset): String = {
    val s = scala.io.Source.fromInputStream(in, charset.toString)
    try {
      s.getLines().mkString
    } finally {
      if (s ne null) s.close()
    }
  }

  def write(in: InputStream, out: OutputStream): Unit = write(in, out, Array.ofDim[Byte](1024))

  def write(in: InputStream, out: OutputStream, buf: Array[Byte]): Unit = try {
    var len = in.read(buf)
    while (len > 0) {
      out.write(buf, 0, len)
      len = in.read(buf)
    }
  } finally {
    close(in)
    close(out)
  }

  def close(c: AutoCloseable): Unit = try {
    if (c ne null) c.close()
  } catch {
    case e: Throwable =>
      logger.error(s"close c: $c error", e)
  }

}
