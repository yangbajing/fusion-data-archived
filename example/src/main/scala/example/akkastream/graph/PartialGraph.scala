package example.akkastream.graph

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Balance, Broadcast, Flow, GraphDSL, Keep, Merge, RunnableGraph, Sink, Source}
import akka.stream.{ActorMaterializer, FlowShape, SourceShape}

import scala.concurrent.Future
import scala.io.StdIn

object PartialGraph extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  def partial = GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val B = b.add(Broadcast[Int](2))
    val C = b.add(Merge[Int](2))
    val D = Flow[Int].map(_ + 1)
    val E = b.add(Balance[Int](2))
    val F = b.add(Merge[Int](2))

    C <~ F
    B ~> C ~> F
    B ~> D ~> E ~> F

    FlowShape(B.in, E.out(1))
  }.named("partial")

  // 转换partial从FlowShape到Flow，可访问流DSL（比如：.filter() 函数）
  val flow = Flow.fromGraph(partial)

  val source = Source.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val merge = b.add(Merge[Int](2))
    Source.single(0) ~> merge
    Source(List(2, 3, 4)) ~> merge
    SourceShape(merge.out)
  })

  val sink: Sink[Int, Future[Int]] = Flow[Int].map(_ * 2).drop(10).named("nestedFlow").toMat(Sink.head)(Keep.right)

  val closed: RunnableGraph[Future[Int]] = source.via(flow.filter(_ > 1)).toMat(sink)(Keep.right)

  closed.run().foreach(println)

  StdIn.readLine()
  system.terminate()
}
