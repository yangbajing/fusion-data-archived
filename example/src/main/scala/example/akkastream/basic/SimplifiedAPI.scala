package example.akkastream.basic

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Broadcast, Merge, Sink, Source}

import scala.io.StdIn

class Remotely extends Actor {
  override def receive: Receive = {
    case value => println(s"receive: $value")
  }
}

object SimplifiedAPI extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val merged = Source.combine(Source(List(1)), Source(List(2)))(Merge(_))
  val mergedResult = merged.runWith(Sink.fold(0)(_ + _))
  mergedResult.foreach(println)

  val sendRemotely =
    Sink.actorRef(system.actorOf(Props[Remotely], "remotely"), "Done")
  val localProcessing = Sink.foreach[Int](v => println(s"foreach($v)"))
  Source(List(0, 1, 1))
    .runWith(Sink.combine(sendRemotely, localProcessing)(strategy =>
      Broadcast[Int](strategy)))

  StdIn.readLine()
  system.terminate()
}
