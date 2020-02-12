package mass.message.job

import fusion.json.CborSerializable

sealed trait JobEvent extends CborSerializable

case class JobExecutionEvent(key: String) extends JobEvent
