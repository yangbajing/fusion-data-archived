package sample.remote.benchmark

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

class Receiver extends Actor {
  import Sender._

  override def receive: Receive = {
    case m: Echo =>
      sender() ! m
    case Shutdown =>
      context.system.terminate()
    case _ =>
    // 接收到的实际消息数据被忽略掉
  }

}

object Receiver extends App {
  val system = ActorSystem("Sys", ConfigFactory.load("remotelookup"))
  system.actorOf(Props[Receiver], "rcv") ! "Hello Scala!"
}
