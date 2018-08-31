package example.akkastream.basic

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}

import scala.collection.immutable
import scala.io.StdIn

object Graph1 extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val graph = g(1 to 2)

  graph.run()

  StdIn.readLine()
  system.terminate()

  def g(data: immutable.Iterable[Int]) =
    RunnableGraph.fromGraph(GraphDSL.create() { implicit b: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._
      val in = Source(data)
      val out = Sink.foreach(println)

      val bcast = b.add(Broadcast[Int](2))
      val merge = b.add(Merge[Int](2))

      val f1, f2, f3, f4 = Flow[Int].map(_ + 10)

      in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
      bcast ~> f4 ~> merge

      ClosedShape
    })

}
