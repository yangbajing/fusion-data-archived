package mass.model.job

import java.time.OffsetDateTime

import fusion.json.CborSerializable

case class TriggerLog(
    id: String,
    jobKey: String,
    startTime: OffsetDateTime,
    completionTime: Option[OffsetDateTime],
    completionStatus: RunStatus,
    completionValue: Option[String],
    createdAt: OffsetDateTime = OffsetDateTime.now())
    extends CborSerializable
