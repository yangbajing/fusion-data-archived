package mass.scheduler.business.actors

import akka.actor.{Actor, Props}
import akka.pattern._
import mass.scheduler.SchedulerSystem
import mass.scheduler.model.JobPageReq
import mass.scheduler.repository.JobRepo

object JobActor {
  val job = 'job

  def props(schedulerSystem: SchedulerSystem): (Props, Symbol) =
    Props(new JobActor(schedulerSystem)) -> job
}

class JobActor(schedulerSystem: SchedulerSystem) extends Actor {
  import context.dispatcher

  val db = schedulerSystem.massSystem.slickDatabase

  override def receive: Receive = {
    case req: JobPageReq => db.run(JobRepo.page(req)).pipeTo(sender())
  }

}
