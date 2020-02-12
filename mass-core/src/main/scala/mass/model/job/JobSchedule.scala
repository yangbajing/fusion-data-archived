package mass.model.job

import java.time.OffsetDateTime

import fusion.json.CborSerializable
import mass.model.CommonStatus

case class JobSchedule(
    key: String,
    item: JobItem,
    trigger: JobTrigger,
    description: String,
    runStatus: RunStatus,
    status: CommonStatus,
    creator: String,
    createdAt: OffsetDateTime,
    scheduleCount: Long = 1,
    lastScheduleStart: Option[OffsetDateTime] = None,
    lastScheduledAt: Option[OffsetDateTime] = None)
    extends CborSerializable
