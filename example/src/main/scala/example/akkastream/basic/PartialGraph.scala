package example.akkastream.basic

import akka.actor.ActorSystem
import akka.stream.scaladsl.{ GraphDSL, RunnableGraph, Sink, Source, ZipWith }
import akka.stream.{ ActorMaterializer, ClosedShape, UniformFanInShape }

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object PartialGraph extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val pickMaxOfThree = GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    // ZipWith 最后一个泛型是输出参数类型。
    val zip1 = b.add(ZipWith[Int, Int, Int](math.max))
    val zip2 = b.add(ZipWith[Int, Int, Int](math.max))
    zip1.out ~> zip2.in0
    UniformFanInShape(zip2.out, zip1.in0, zip1.in1, zip2.in1)
  }

  val resultSink = Sink.head[Int]

  val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit b => sink =>
    import GraphDSL.Implicits._

    val pm3 = b.add(pickMaxOfThree)

    Source.single(4) ~> pm3.in(0)
    Source.single(2) ~> pm3.in(1)
    Source.single(3) ~> pm3.in(2)
    pm3.out ~> sink.in

    ClosedShape
  })

  val result = Await.result(g.run, 300.millis)
  println(s"result: $result")

  StdIn.readLine()
  system.terminate()
}
