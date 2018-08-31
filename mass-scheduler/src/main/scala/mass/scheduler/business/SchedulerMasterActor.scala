package mass.scheduler.business
import akka.actor.{Actor, ActorRef, ActorRefFactory, Props, Status}
import helloscala.common.exception.HSNotFoundException
import mass.scheduler.SchedulerSystem
import mass.scheduler.business.actors.JobActor
import mass.scheduler.model.JobMessage

class SchedulerMasterActor(
    schedulerSystem: SchedulerSystem,
    propsList: Iterable[(Props, Symbol)]
) extends Actor {
  var actors = Map[Symbol, ActorRef]()

  override def preStart(): Unit =
    actors = propsList.map {
      case (props, name) =>
        name -> context.actorOf(props, name.toString())
    }.toMap

  override def postStop(): Unit =
    actors.valuesIterator.foreach(actor => context.stop(actor))

  override def receive: Receive = {
    case (name: Symbol, msg) => sendMessage(name, msg)
    case msg: JobMessage     => sendMessage(JobActor.job, msg)
  }

  def sendMessage(name: Symbol, msg: Any): Unit =
    actors.get(name) match {
      case Some(actor) => actor forward msg
      case None =>
        sender() ! Status.Failure(HSNotFoundException(s"actor $name 未找到"))
    }
}

object SchedulerMasterActor {
  val name = 'scheduler

  def props(
      schedulerSystem: SchedulerSystem,
      propsList: Iterable[(Props, Symbol)],
      name: String
  )(ref: ActorRefFactory): ActorRef = {
    require(propsList.groupBy(_._2).size == propsList.size, "propsList有重复的名字")
    ref.actorOf(Props(new SchedulerMasterActor(schedulerSystem, propsList)), name)
  }

}
