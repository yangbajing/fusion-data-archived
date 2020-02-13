package mass.model.job

import fusion.json.CborSerializable
import helloscala.common.Configuration
import helloscala.common.exception.HSBadRequestException
import mass.core.ProgramVersion

// #JobItem
case class JobItem(
    program: Program,
    programOptions: Seq[String],
    programMain: String,
    programArgs: Seq[String] = Seq(),
    programVersion: String = "",
    resources: Map[String, String] = Map(),
    data: Map[String, String] = Map(),
    description: Option[String] = None,
    dependentJobKeys: Seq[String] = Nil,
    name: Option[String] = None)
    extends CborSerializable
// #JobItem

object JobItem {
  def apply(item: Configuration): JobItem = {
    val program = Program.optionalFromName(item.getString("program").toUpperCase()).orElse(Program.UNKOWN)
    val programMain = item.getString("program-main")
    val _version = item.getOrElse[String]("program-version", "")
    val programVersion =
      ProgramVersion
        .get(program, _version)
        .getOrElse(
          throw HSBadRequestException(s"Configuration key program-version is invalid, current value is ${_version}."))
    JobItem(
      program,
      item.getOrElse[Seq[String]]("program-options", Nil),
      programMain,
      item.getOrElse[Seq[String]]("program-args", Nil),
      programVersion.version,
      name = item.get[Option[String]]("name"))
  }
}
