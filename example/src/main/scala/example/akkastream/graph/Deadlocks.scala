package example.akkastream.graph

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape, OverflowStrategy}
import akka.stream.scaladsl.{Broadcast, Concat, Flow, GraphDSL, Merge, MergePreferred, RunnableGraph, Sink, Source, ZipWith}

import scala.io.StdIn

object Deadlocks extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val source = Source(1 to 10)

  /*
  // WARNING! The graph below deadlocks!
  RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
        val merge = b.add(Merge[Int](2))
    val bcast = b.add(Broadcast[Int](2))
    source ~> merge ~> Flow[Int].map { s => println(s); s } ~> bcast ~> Sink.ignore
    merge <~ bcast
    ClosedShape
  }).run()

  // WARNING! The graph below stops consuming from "source" after a few steps
  // 避免了死锁，但处理不能停止。缓冲区一直未恢复，看到一直在循环处理初使数据（1, 2）
  RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val merge = b.add(MergePreferred[Int](1))
    val bcast = b.add(Broadcast[Int](2))
    source ~> merge ~> Flow[Int].map { s => println(s); s } ~> bcast ~> Sink.ignore
    merge.preferred <~ bcast
    ClosedShape
  }).run()
*/
  /*
  RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val merge = b.add(Merge[Int](2))
    val bcast = b.add(Broadcast[Int](2))
    source ~> merge ~> Flow[Int].map { s => println(s); s } ~> bcast ~> Sink.ignore
    merge <~ Flow[Int].buffer(10, OverflowStrategy.dropHead) <~ bcast
    ClosedShape
  }).run()
*/
  /*
  RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val zip = b.add(ZipWith[Int, Int, Int]((left, right) => right))
    val bcast = b.add(Broadcast[Int](2))
    source ~> zip.in0
    zip.out.map { s => println(s); s } ~> bcast ~> Sink.ignore
    zip.in1 <~ bcast
    ClosedShape
  }).run()
*/

  // Success
  RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val zip = b.add(ZipWith((left: Int, right: Int) => {
      println(s"left: $left, right: $right")
      left
    }))
    val bcast = b.add(Broadcast[Int](2))
    val concat = b.add(Concat[Int]())
    val start = Source.single(0)
    source ~> zip.in0
    zip.out.map { s => println(s); s } ~> bcast ~> Sink.ignore
    zip.in1 <~ concat <~ start /*source //会有死锁*/
    concat <~ bcast
    ClosedShape
  }).run()

  StdIn.readLine()
  system.terminate()
}
