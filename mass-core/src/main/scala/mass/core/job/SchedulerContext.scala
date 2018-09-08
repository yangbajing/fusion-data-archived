package mass.core.job

import akka.actor.ActorSystem
import mass.model.job.JobItem

case class SchedulerContext(
    key: String,
    jobItem: JobItem,
    @Deprecated triggerKey: String,
    data: Map[String, String],
    system: ActorSystem)
