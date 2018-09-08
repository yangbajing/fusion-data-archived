/*
 * Copyright 2018 羊八井(yangbajing)（杨景）
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helloscala.common.util

import java.io.{IOException, InputStream, OutputStream}
import java.nio.charset.Charset
import java.nio.file.{Files, Path, StandardOpenOption}
import java.time.OffsetDateTime

import com.typesafe.scalalogging.StrictLogging

import scala.sys.process.{Process, ProcessLogger}

object FileUtils extends StrictLogging {

  def getString(in: InputStream, charset: Charset, lineSplit: String = ""): String = {
    val s = scala.io.Source.fromInputStream(in, charset.toString)
    try {
      s.getLines().mkString(lineSplit)
    } finally {
      if (s ne null) s.close()
    }
  }

  @throws[IOException]("IO异常")
  def write(in: InputStream, out: OutputStream): Unit =
    write(in, out, Array.ofDim[Byte](1024))

  @throws[IOException]("IO异常")
  def write(in: InputStream, out: OutputStream, buf: Array[Byte]): Unit =
    try {
      var len = in.read(buf)
      while (len > 0) {
        out.write(buf, 0, len)
        len = in.read(buf)
      }
    } finally {
      close(in)
      close(out)
    }

  def close(c: AutoCloseable): Unit =
    try {
      if (c ne null) c.close()
    } catch {
      case e: Throwable =>
        logger.error(s"close c: $c error", e)
    }

  def processBuilder(
      commands: Seq[String],
      dist: Path,
      extraEnvs: Seq[(String, String)] = Nil,
      outPath: Option[Path] = None,
      errPath: Option[Path] = None
  ): MassProcessBuilder = {
    val start = OffsetDateTime.now()
    val prefix = start.format(TimeUtils.formatterDateTime)
    val _outPath = outPath getOrElse Files.createTempFile(dist, prefix, "out")
    val _errPath = errPath getOrElse Files.createTempFile(dist, prefix, "err")
    new MassProcessBuilder(commands, dist, extraEnvs, start, _outPath, _errPath)
  }

}

class MassProcessBuilder(
    commands: Seq[String],
    dist: Path,
    extraEnvs: Seq[(String, String)],
    val start: OffsetDateTime,
    val outPath: Path,
    val errPath: Path
) extends Process
    with StrictLogging {
  if (!Files.isDirectory(outPath.getParent)) {
    Files.createDirectories(outPath.getParent)
  }
  if (!Files.isDirectory(errPath.getParent)) {
    Files.createDirectories(errPath.getParent)
  }
  private val outWriter = Files.newBufferedWriter(outPath, StandardOpenOption.APPEND, StandardOpenOption.CREATE)
  private val errWriter = Files.newBufferedWriter(errPath, StandardOpenOption.APPEND, StandardOpenOption.CREATE)

  val envs = extraEnvs.map {
    case (name, value) => name -> StringUtils.option(System.getenv(name)).map(v => s"$value:$v").getOrElse(value)
  }

  println("PATH: " + System.getenv("PATH"))

  logger.info(s"$commands\t$dist\5$envs\t$start\t$outPath\t$errPath")

  private val p = Process(commands, dist.toFile, envs: _*).run(ProcessLogger(fout => {
    outWriter.write(fout)
    outWriter.flush()
  }, ferr => {
    errWriter.write(ferr)
    errWriter.flush()
  }))

  override def isAlive(): Boolean = p.isAlive()
  override def exitValue(): Int = p.exitValue()

  override def destroy(): Unit = {
    outWriter.newLine()
    errWriter.newLine()
    FileUtils.close(outWriter)
    FileUtils.close(errWriter)
    p.destroy()
  }

}
