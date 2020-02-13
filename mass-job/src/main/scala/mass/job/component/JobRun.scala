package mass.job.component

import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Path }
import java.time.OffsetDateTime
import java.util.stream.Collectors

import com.typesafe.scalalogging.StrictLogging
import helloscala.common.exception.HSBadRequestException
import mass.common.util.FileUtils
import mass.core.{ Constants, ProgramVersion }
import mass.core.job.JobConstants
import mass.job.util.JobUtils
import mass.job.JobSettings
import mass.message.job.SchedulerJobResult
import mass.model.job.{ JobItem, Program }

/**
 * 阻塞API
 */
object JobRun extends StrictLogging {
  val MAX_DEPTH = 10

  def runOnZip(zipPath: Path, key: String, item: JobItem, jobSettings: JobSettings): SchedulerJobResult = {
    val dir = jobSettings.jobRunDir.resolve(key)
    val dist = dir.resolve(JobConstants.DIST)
    if (!Files.isDirectory(dist)) {
      Files.createDirectories(dist)
      JobUtils.parseJobZip(zipPath, StandardCharsets.UTF_8, dist) match {
        case Left(e) => throw e
        case _       =>
      }
    }
    run(dist, key, item, jobSettings)
  }

  def run(item: JobItem, key: String, jobSettings: JobSettings): SchedulerJobResult =
    run(jobSettings.getJobRunDist(key), key, item, jobSettings)

  def run(dist: Path, key: String, item: JobItem, jobSettings: JobSettings): SchedulerJobResult = {
    if (!Files.isDirectory(dist)) {
      Files.createDirectories(dist)
    }
    val (commands, envs) = parseCommands(item, jobSettings, dist)
    commands match {
      case _ if commands.isEmpty =>
        throw HSBadRequestException(s"无效的程序类型，key：$key。")
      case _ =>
        val logDist = jobSettings.jobRunDir.resolve(key)
        if (!Files.isDirectory(logDist)) {
          Files.createDirectories(logDist)
        }
        run(
          commands ++ Seq(item.programMain) ++ item.programArgs,
          dist,
          envs,
          Some(logDist.resolve(Constants.OUT_LOG_SUFFIX)),
          Some(logDist.resolve(Constants.ERR_LOG_SUFFIX)))
    }
  }

  def run(
      commands: Seq[String],
      dist: Path,
      extraEnvs: Seq[(String, String)] = Nil,
      outPath: Option[Path] = None,
      errPath: Option[Path] = None): SchedulerJobResult = {
    val p = FileUtils.processBuilder(commands, dist, extraEnvs, outPath, errPath)
    try {
      val exitValue = p.exitValue()
      val end = OffsetDateTime.now()
      SchedulerJobResult(p.start, Some(end), exitValue, p.outPath.toString, p.errPath.toString)
    } finally {
      p.destroy()
    }
  }

  /**
   *
   * @return (commands, environments)
   */
  private def parseCommands(
      item: JobItem,
      schedulerConfig: JobSettings,
      dist: Path): (Seq[String], Seq[(String, String)]) =
    item.program match {
      case Program.SCALA =>
        val options =
          if (item.programOptions.exists(item => item == "-cp" || item == "-classpath")) {
            item.programOptions ++ Seq("-cp", schedulerConfig.schedulerRunJar)
          } else {
            val classpath = Files
              .walk(dist, MAX_DEPTH)
              .filter(_.endsWith(".jar"))
              .map[String](_.toString)
              .collect(Collectors.joining(":", "", s":./:${schedulerConfig.schedulerRunJar}"))
            Seq("-classpath", classpath) ++ item.programOptions
          }
        val version = ProgramVersion.get(item.program, item.programVersion).getOrElse(ProgramVersion.Scala212)
        val cmd = version match {
          case ProgramVersion.Scala211 => schedulerConfig.massSettings.compiles.scala212Home + "/bin/" + version.cli
          case _                       => schedulerConfig.massSettings.compiles.scala212Home + "/bin/" + version.cli
        }
        (Seq(cmd) ++ options, Nil)
      case Program.JAVA =>
        val options =
          if (item.programOptions.exists(item => item == "-cp" || item == "-classpath")) {
            item.programOptions ++ Seq("-cp", schedulerConfig.schedulerRunJar)
          } else {
            val classpath = Files
              .walk(dist, MAX_DEPTH)
              .filter(_.endsWith(".jar"))
              .map[String](_.toString)
              .collect(Collectors.joining(":", "", s":./:${schedulerConfig.schedulerRunJar}"))
            Seq("-classpath", classpath) ++ item.programOptions
          }
        (Seq("java") ++ options, Nil)
      case Program.PYTHON =>
        val cmd = ProgramVersion.getCliOrElse(item.program, item.programVersion, "python")
        (Seq(cmd) ++ item.programOptions, Seq("PYTHONPATH" -> "./"))
      case Program.SH =>
        val cmd = ProgramVersion.getCliOrElse(item.program, item.programVersion, "bash")
        (Seq(cmd) ++ item.programOptions, Nil)
      case Program.SQL =>
        val cmd = ProgramVersion
          .getCli(item.program, item.programVersion)
          .getOrElse(throw HSBadRequestException(s"SQL执行程序不存在。${item.program}${item.programVersion}"))
        (Seq(cmd) ++ item.programOptions, Seq(("PATH", System.getProperty("user.dir") + "/bin")))
      case _ =>
        (Nil, Nil)
    }
}
