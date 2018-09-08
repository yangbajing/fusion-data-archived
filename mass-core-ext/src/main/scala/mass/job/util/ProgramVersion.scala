package mass.job.util

import mass.model.job.Program

sealed trait ProgramVersion {
  val NAME: Program
  val VERSION: String
  def CLI: String = NAME.name.toLowerCase + VERSION
}

object ProgramVersion {
  case object Scala211 extends ProgramVersion {
    override val NAME: Program = Program.SCALA
    override val VERSION: String = "2.11"
    override def CLI: String = NAME.name.toLowerCase
  }
  case object Scala212 extends ProgramVersion {
    override val NAME: Program = Program.SCALA
    override val VERSION: String = "2.12"
    override def CLI: String = NAME.name.toLowerCase
  }
  case object Java7 extends ProgramVersion {
    override val NAME: Program = Program.JAVA
    override val VERSION: String = "7"
  }
  case object Java8 extends ProgramVersion {
    override val NAME: Program = Program.JAVA
    override val VERSION: String = "8"
  }
  case object Python2_7 extends ProgramVersion {
    override val NAME: Program = Program.PYTHON
    override val VERSION: String = "2.7"
  }
  case object Python3_6 extends ProgramVersion {
    override val NAME: Program = Program.PYTHON
    override val VERSION: String = "3.6"
  }
  case object Bash extends ProgramVersion {
    override val NAME: Program = Program.SH
    override val VERSION: String = "Bash"
    override val CLI: String = "bash"
  }
  case object Sh extends ProgramVersion {
    override val NAME: Program = Program.SH
    override val VERSION: String = "sh"
    override val CLI: String = "sh"
  }
  case object SqlJdbc extends ProgramVersion {
    override val NAME: Program = Program.SQL
    override val VERSION: String = "JDBC"
    override val CLI: String = "mass-jdbc-cli"
  }
  case object SqlPostgres extends ProgramVersion {
    override val NAME: Program = Program.SQL
    override val VERSION: String = "PostgreSQL"
    override val CLI: String = "psql"
  }
  case object SqlMySQL extends ProgramVersion {
    override val NAME: Program = Program.SQL
    override val VERSION: String = "MySQL"
    override val CLI: String = "mysql"
  }

  val values = Vector(Scala212, Scala211, Java8, Java7, Python3_6, Python2_7, Bash, Sh, SqlJdbc, SqlPostgres, SqlMySQL)

  def get(program: Program, version: String): Option[ProgramVersion] = {
    values
      .find(pv => pv.NAME == program && pv.VERSION == version)
      .orElse(Option(program match {
        case Program.SCALA  => Scala212
        case Program.JAVA   => Java8
        case Program.PYTHON => Python3_6
        case Program.SQL    => SqlJdbc
        case Program.SH     => Bash
        case _              => null
      }))
  }

  def getString(program: Program, version: String): Option[String] = get(program, version).map(_.CLI)

  @inline def getStringOrElse(program: Program, version: String, deft: => String): String =
    getString(program, version).getOrElse(deft)

}
