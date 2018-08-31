package example.akkastream.basic

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Keep, RunnableGraph, Sink, Source}

import scala.collection.immutable
import scala.concurrent.Future
import scala.io.StdIn

object Graph3 extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val sinks: immutable.Seq[Sink[String, Future[String]]] =
    List("a", "b", "c").map(
      prefix =>
        Flow[String]
          .filter(str => str.startsWith(prefix))
          .toMat(Sink.head[String])(Keep.right))

  val g = RunnableGraph.fromGraph(GraphDSL.create(sinks) { implicit b => sinkList =>
    import GraphDSL.Implicits._

    val broadcast = b.add(Broadcast[String](sinkList.size))

    Source(List("ax", "bx", "cx")) ~> broadcast
    sinkList.foreach(sink => broadcast ~> sink)

    ClosedShape
  })

  val matList: immutable.Seq[Future[String]] = g.run()

  Future.sequence(matList).foreach(println)

  StdIn.readLine()
  system.terminate()
}
