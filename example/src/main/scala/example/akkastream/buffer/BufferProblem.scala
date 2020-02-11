package example.akkastream.buffer
import akka.actor.ActorSystem
import akka.stream.scaladsl.{ GraphDSL, RunnableGraph, Sink, Source, ZipWith }
import akka.stream.{ ActorMaterializer, Attributes, ClosedShape }

import scala.concurrent.duration._
import scala.io.StdIn

object BufferProblem extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  case class Tick()

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import akka.stream.scaladsl.GraphDSL.Implicits._

    // this is the asynchronous stage in this graph
    val zipper =
      b.add(ZipWith[Tick, Int, Int]((tick, count) => count).async.addAttributes(Attributes.inputBuffer(1, 1)))
    // 用默认缓冲区设置时将只打印 1
    //    val zipper = b.add(ZipWith[Tick, Int, Int]((tick, count) => count).async)

    Source.tick(initialDelay = 3.second, interval = 3.second, Tick()) ~> zipper.in0

    Source
      .tick(initialDelay = 1.second, interval = 1.second, "message!")
      .conflateWithSeed(seed = (_) => 1)((count, _) => count + 1) ~> zipper.in1

    zipper.out ~> Sink.foreach(println)
    ClosedShape
  })

  g.run()

  StdIn.readLine()
  system.terminate()
}
