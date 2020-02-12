package mass.model.job

import fusion.json.CborSerializable

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
    dependentJobKeys: Seq[String] = Nil)
    extends CborSerializable
// #JobItem
