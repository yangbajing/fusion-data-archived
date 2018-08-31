package example.akkastream.graph

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source, Tcp}
import akka.util.ByteString

import scala.concurrent.{Future, Promise}

object MaterializeValue {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  case class MyClass(private val p: Promise[Option[Int]], conn: Tcp.OutgoingConnection) extends AutoCloseable {
    override def close(): Unit = p.trySuccess(None)
  }

  // Materializes to Promise[Option[Int]]
  val source: Source[Int, Promise[Option[Int]]] = Source.maybe[Int]

  // Materializes to NotUsed
  val flow1: Flow[Int, Int, NotUsed] = Flow[Int].take(100)

  // Materializes to Promise[Int]
  val nestedSource: Source[Int, Promise[Option[Int]]] = source
    .viaMat(flow1)(Keep.left)
    .named("nestedSource") // viaMat === via()(Keep.left)
  //  val nestedSource2: Source[Int, NotUsed] = source.viaMat(flow1)(Keep.right)

  // Materializes to NotUsed
  val flow2: Flow[Int, ByteString, NotUsed] =
    Flow[Int].map(i => ByteString(i.toString))

  // Materializes to Future[Tcp.OutgoingConnection   (Keep.right)
  val flow3: Flow[ByteString, ByteString, Future[Tcp.OutgoingConnection]] =
    Tcp().outgoingConnection("localhost", 8080)

  val nestedFlow: Flow[Int, ByteString, Future[Tcp.OutgoingConnection]] =
    flow2.viaMat(flow3)(Keep.right)

  val nestedFlow2: Flow[Int, ByteString, NotUsed] =
    flow2.viaMat(flow3)(Keep.left) // flow2.via(flow3)
  val nestedFlow3: Flow[Int, ByteString, (NotUsed, Future[Tcp.OutgoingConnection])] =
    flow2.viaMat(flow3)(Keep.both)

  // Materializes to Future[String]   (Keep.right)
  val sink: Sink[ByteString, Future[String]] =
    Sink.fold[String, ByteString]("")(_ + _.utf8String)

  val nestedSink: Sink[Int, (Future[Tcp.OutgoingConnection], Future[String])] =
    nestedFlow.toMat(sink)(Keep.both)

  def f(p: Promise[Option[Int]], rest: (Future[Tcp.OutgoingConnection], Future[String])): Future[MyClass] = {
    val connFuture = rest._1
    connFuture.map(outConn => MyClass(p, outConn))
  }

  // Materializes to Future[MyClass]
  val runnableGraph: RunnableGraph[Future[MyClass]] =
    nestedSource.toMat(nestedSink)(f)

  val r: RunnableGraph[Promise[Option[Int]]] =
    nestedSource.toMat(nestedSink)(Keep.left)

  val r2: RunnableGraph[(Future[Tcp.OutgoingConnection], Future[String])] =
    nestedSource.toMat(nestedSink)(Keep.right)
}
