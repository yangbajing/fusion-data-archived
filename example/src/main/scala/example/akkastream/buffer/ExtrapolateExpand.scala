package example.akkastream.buffer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Source }

import scala.io.StdIn

object ExtrapolateExpand extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  //  val lastFlow = Flow[Double].extrapolate(Iterator.continually(_))
  //  Source((1 to 10).map(_.toDouble)).via(lastFlow).runWith(Sink.foreach(println))

  //  val initial = 2.0
  //  val seedFlow = Flow[Double].extrapolate(Iterator.continually(_), Some(initial))
  //  Source((1 to 10).map(_.toDouble)).via(seedFlow).runWith(Sink.foreach(println))

  //  val driftFlow = Flow[Double].map(_ -> 0).extrapolate[(Double, Int)] { case (i, _) => Iterator.from(1).map(i -> _) }
  //  Source((1 to 10).map(_.toDouble)).via(driftFlow).runForeach(println)

  val driftFlow = Flow[Double].expand(i => Iterator.from(0).map(i -> _))
  Source((1 to 10).map(_.toDouble)).via(driftFlow).runForeach(println)

  StdIn.readLine()
  system.terminate()
}
