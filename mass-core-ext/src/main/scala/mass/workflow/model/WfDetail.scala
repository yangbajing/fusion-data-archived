package mass.workflow.model

import java.time.OffsetDateTime

case class WfDetail(
    name: String,
    content: String,
    createdAt: OffsetDateTime)
