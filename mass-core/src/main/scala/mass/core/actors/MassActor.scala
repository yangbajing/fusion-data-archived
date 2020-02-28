//package mass.core.actors
//
//import akka.actor.SupervisorStrategy._
//import akka.actor.{ Actor, OneForOneStrategy, SupervisorStrategy }
//import com.typesafe.scalalogging.StrictLogging
//import helloscala.common.exception.HSException
//
///**
// * 为Actor特质的一些功能添加了默认实现
// */
//trait MassActor extends Actor with StrictLogging {
//  override def supervisorStrategy: SupervisorStrategy = {
//    OneForOneStrategy() {
//      case _: HSException => Resume
//      case other          => defaultDecider(other)
//    }
//  }
//}
