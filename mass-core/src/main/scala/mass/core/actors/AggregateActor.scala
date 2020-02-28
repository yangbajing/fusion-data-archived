//package mass.core.actors
//
//import akka.actor.{ ActorRef, Props, Status }
//import helloscala.common.exception.{ HSBadRequestException, HSNotFoundException }
//import helloscala.common.util.StringUtils
//
///**
// * 聚合根Actor，通过 Iterable[(Props, Symbol)]将多个业务Actor聚合到一起提供一个门面供上层调用。
// */
//trait AggregateActor extends MassActor {
//  val propsList: Iterable[(Props, Symbol)]
//  protected var actors: Map[Symbol, ActorRef] = Map()
//
//  override def preStart(): Unit = {
//    require(propsList.groupBy(_._2).size == propsList.size, "propsList有重复的名字")
//    actors = propsList.map { case (props, symbol) => symbol -> context.actorOf(props, symbol.name) }.toMap
//  }
//
//  override def postStop(): Unit = {
//    actors.valuesIterator.foreach(actor => context.stop(actor))
//    actors = Map()
//  }
//
//  override def receive: Receive = {
//    case (name: Symbol, msg) => sendMessage(name, msg)
//    case msg => // 将消息名的第一部分解析出来作为 Symbol
//      StringUtils.extractFirstName(msg) match {
//        case Some(name) => sendMessage(Symbol(name), msg)
//        case _          => sender() ! Status.Failure(HSBadRequestException(s"未找到可处理此消息的服务，发送消息为：$msg"))
//      }
//  }
//
//  private def sendMessage(name: Symbol, msg: Any): Unit =
//    actors.get(name) match {
//      case Some(actor) => actor forward msg
//      case None        => sender() ! Status.Failure(HSNotFoundException(s"服务: $name 未找到，发送消息为：$msg"))
//    }
//}
