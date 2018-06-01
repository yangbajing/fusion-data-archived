package example.akkastream

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{ActorMaterializer, DelayOverflowStrategy, KillSwitches}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration._
import scala.io.StdIn

object KillSwitch extends App with StrictLogging {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val countingSrc = Source(Stream.from(1)).delay(1.second, DelayOverflowStrategy.backpressure)

  countingSrc.runForeach(i => logger.info(s"run: $i"))

  val lastSnk = Sink.last[Int]

  val (killSwitch, last) = countingSrc
    .viaMat(KillSwitches.single)(Keep.right)
    .toMat(lastSnk)(Keep.both)
    .run()

  Thread.sleep(7000)

  killSwitch.shutdown()

  last.foreach(println)

  StdIn.readLine()
  system.terminate()
}
