package mass.core

import mass.model.job.Program

sealed abstract class ProgramVersion(val program: Program, val version: String, val cli: String) {
  def this(name: Program, version: String) = this(name, version, name.name.toLowerCase + version)
}

object ProgramVersion {
  case object Scala211 extends ProgramVersion(Program.SCALA, "2.11", "scala")
  case object Scala212 extends ProgramVersion(Program.SCALA, "2.12", "scala")
  case object Scala213 extends ProgramVersion(Program.SCALA, "2.13", "scala")
  case object Java7 extends ProgramVersion(Program.JAVA, "7", "java")
  case object Java8 extends ProgramVersion(Program.JAVA, "8", "java")
  case object Java11 extends ProgramVersion(Program.JAVA, "11", "java")
  case object Python2 extends ProgramVersion(Program.PYTHON, "2")
  case object Python3 extends ProgramVersion(Program.PYTHON, "3")
  case object Bash extends ProgramVersion(Program.SH, "Bash", "bash")
  case object Sh extends ProgramVersion(Program.SH, "SH", "sh")
  case object SqlJdbc extends ProgramVersion(Program.SQL, "JDBC", "mass-jdbc-cli")
  case object SqlPostgres extends ProgramVersion(Program.SQL, "Postgre", "psql")
  case object SqlMySQL extends ProgramVersion(Program.SQL, "MySQL", "mysql")

  val values = Vector(
    Scala213,
    Scala212,
    Scala211,
    Java11,
    Java8,
    Java7,
    Python3,
    Python2,
    Bash,
    Sh,
    SqlJdbc,
    SqlPostgres,
    SqlMySQL)

  def get(program: Program, version: String): Option[ProgramVersion] = {
    values
      .find(pv => pv.program == program && pv.version == version)
      .orElse(Option(program match {
        case Program.SCALA  => Scala212
        case Program.JAVA   => Java8
        case Program.PYTHON => Python3
        case Program.SQL    => SqlJdbc
        case Program.SH     => Bash
        case _              => null
      }))
  }

  def getCli(program: Program, version: String): Option[String] = get(program, version).map(_.cli)

  @inline def getCliOrElse(program: Program, version: String, deft: => String): String =
    getCli(program, version).getOrElse(deft)
}
