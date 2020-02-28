package mass.message.job

import fusion.json.CborSerializable

sealed trait JobEvent extends CborSerializable

case class JobTriggerEvent(key: String) extends JobEvent
