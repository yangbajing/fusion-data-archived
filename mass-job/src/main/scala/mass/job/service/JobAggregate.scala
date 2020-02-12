//package mass.job.service
//
//import akka.actor.Props
//import mass.core.actors.AggregateActor
//
//class JobAggregate(val propsList: Iterable[(Props, Symbol)]) extends AggregateActor
//
//object JobAggregate {
//  val name = 'job
//
//  def props(propsList: Iterable[(Props, Symbol)]): Props = Props(new JobAggregate(propsList))
//}
