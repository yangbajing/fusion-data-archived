/**
 * http://blog.colinbreck.com/maximizing-throughput-for-akka-streams/
 */
package example.motivating

import java.security.SecureRandom
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration._
import scala.io.StdIn

object MaximizingThroughputDemo extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val random = new SecureRandom()
  TimeUnit.SECONDS.sleep(5)

  def spin(value: Int): Int = {
    val start = System.currentTimeMillis()
    while ((System.currentTimeMillis() - start) < 100) {}
    value
  }

  /*Source(1 to 1000)
    .map(spin)
    //    .async
    .map(spin)
    .runWith(Sink.ignore)*/
  /*Source(1 to 1000000)
    .mapAsync(4)(x => Future(spin(x)))
    .mapAsync(4)(x => Future(spin(x)))
    .runWith(Sink.ignore)*/

  /*// Simulate a non-uniform CPU-bound workload
  def uniformRandomSpin(value: Int): Future[Int] = Future {
    val max = random.nextInt(101)
    val start = System.currentTimeMillis()
    while ((System.currentTimeMillis() - start) < max) {}
    value
  }
  Source(1 to 1000)
    .mapAsync(1)(uniformRandomSpin).async
    .mapAsync(1)(uniformRandomSpin).async
    .mapAsync(1)(uniformRandomSpin).async
    .mapAsync(1)(uniformRandomSpin).async
    .runWith(Sink.ignore)*/

  // Simulate a non-blocking network call to another service
  def nonBlockingCall(value: Int): Future[Int] = {
    val promise = Promise[Int]

    system.scheduler.scheduleOnce(random.nextInt(101).milliseconds) {
      promise.success(value)
    }

    promise.future
  }

  Source(1 to 1000)
    .mapAsync(1000)(nonBlockingCall)
    .runWith(Sink.ignore)

  StdIn.readLine()
  system.terminate()

}
