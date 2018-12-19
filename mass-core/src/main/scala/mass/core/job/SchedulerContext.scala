package mass.core.job

import akka.actor.ActorSystem
import mass.model.job.JobItem

case class SchedulerContext(key: String, jobItem: JobItem, data: Map[String, String], system: ActorSystem)
