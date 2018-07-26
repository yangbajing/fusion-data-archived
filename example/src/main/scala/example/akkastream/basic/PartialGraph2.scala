package example.akkastream.basic

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Sink, Source, Zip}
import akka.stream.{ActorMaterializer, FlowShape, SourceShape}

import scala.io.StdIn

object PartialGraph2 extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val pairs: Source[(Int, Int), NotUsed] = Source.fromGraph(GraphDSL.create() {
    implicit b ⇒
      import GraphDSL.Implicits._

      // prepare graph elements
      val zip = b.add(Zip[Int, Int]())

      def ints = Source.fromIterator(() ⇒ Iterator.from(1))

      // connect the graph
      ints.filter(_ % 2 != 0) ~> zip.in0
      ints.filter(_ % 2 == 0) ~> zip.in1

      // expose port
      SourceShape(zip.out)
  })

  val firstPair = pairs.runWith(Sink.head)
  firstPair.foreach(println)

  val pairUpWithToString = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val broadcast = b.add(Broadcast[Int](2))
    val zip = b.add(Zip[Int, String]())

    broadcast.out(0) /*.map(identity)*/ ~> zip.in0
    broadcast.out(1).map(_.toString) ~> zip.in1

    FlowShape(broadcast.in, zip.out)
  })

  Source(List(1)).via(pairUpWithToString).runWith(Sink.head).foreach(println)

  StdIn.readLine()
  system.terminate()
}
