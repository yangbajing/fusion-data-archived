/**
 * http://blog.colinbreck.com/patterns-for-streaming-measurement-data-with-akka-streams/
 */
package example.motivating

import java.security.SecureRandom

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration._
import scala.io.StdIn

case class Status()

case class Sample(timestamp: Long, sample: Float)

object Demo extends App with StrictLogging {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val random = new SecureRandom()

  //  Source.single("ddd")
  //    .map(elem => List(elem, elem))
  //    .mapConcat(identity)
  //    .runWith(Sink.foreach(println))

  //  Source.tick(0.millisecond, 10.milliseconds, ())
  //    .map(_ => Sample(System.currentTimeMillis(), random.nextFloat()))
  //    .throttle(elements = 1, per = 1.second, maximumBurst = 1, mode = ThrottleMode.shaping)
  //    .runWith(Sink.foreach(msg => logger.info(s"$msg")))

  // -------------------------------------------------------------------------------------------------------------------

  //  Source.tick(0.millisecond, 10.seconds, ())
  //    .map(_ => Sample(System.currentTimeMillis(), random.nextFloat()))
  // // .idleTimeout(1.second)
  //    .runWith(Sink.foreach(println))
  //    .recover {
  //      case ex: java.util.concurrent.TimeoutException =>
  //      logger.error(s"Device 1 has been idle for 1 minute", ex)
  //    }

  // -------------------------------------------------------------------------------------------------------------------
  val status =
    Source.tick(0.minute, 10.seconds, ())
      .map(_ => Status())

  Source.tick(0.milliseconds, 1.second, ())
    .map(_ => Sample(System.currentTimeMillis(), random.nextFloat()))
    .merge(status)
    .runWith(Sink.foreach(println))

  StdIn.readLine()
  system.terminate()
}
