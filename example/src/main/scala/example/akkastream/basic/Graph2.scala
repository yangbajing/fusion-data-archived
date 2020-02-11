package example.akkastream.basic

import akka.actor.ActorSystem
import akka.stream.{ ActorMaterializer, ClosedShape }
import akka.stream.scaladsl.{ Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source }

import scala.io.StdIn

object Graph2 extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val topHeadSink = Sink.head[Int]
  val bottomHeadSink = Sink.head[Int]
  val sharedDoubler = Flow[Int].map(_ * 2)

  val g = RunnableGraph.fromGraph(GraphDSL.create(topHeadSink, bottomHeadSink)((_, _)) {
    implicit builder => (topHS, bottomHS) =>
      import GraphDSL.Implicits._

      val broadcast = builder.add(Broadcast[Int](2))
      Source.single(1) ~> broadcast.in

      broadcast ~> sharedDoubler ~> topHS.in
      broadcast ~> sharedDoubler ~> bottomHS.in

      ClosedShape
  })

  val (topF, bottomF) = g.run()
  topF.foreach(v => println(s"top is $v"))
  bottomF.foreach(v => println(s"bottom is $v"))

  StdIn.readLine()
  system.terminate()
}
