package example

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import example.Worker.FibonacciNumber

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.io.StdIn

object Worker {

  case class FibonacciNumber(nbr: Int, delay: FiniteDuration)

  case class GetResult(nr: Int, source: ActorRef)

  def props: Props = Props(new Worker)

}

class Worker extends Actor with ActorLogging {

  import Worker._
  import context.dispatcher

  override def preStart(): Unit = {
    log.info(s"$self started")
  }

  override def postStop(): Unit = {
    log.info(s"$self stopped")
  }

  override def receive: Receive = {
    case FibonacciNumber(nr, delay) =>
      context.system.scheduler.scheduleOnce(delay, self, GetResult(nr, sender()))

    case GetResult(nr, source) =>
      val result = fibonacci(nr)
      log.info(s"$nr! = $result")
  }

  private def fibonacci(n: Int): Int = {
    @tailrec
    def fib(n: Int, b: Int, a: Int): Int = n match {
      case 0 => a
      case _ =>
        fib(n - 1, a + b, b)
    }
    fib(n, 1, 0)
  }

}

object BalancingPoolDemo extends App {
  implicit val system = ActorSystem()

  val worker = system.actorOf(Worker.props, "worker")
  worker ! FibonacciNumber(50, 50.millis)
  worker ! FibonacciNumber(33, 50.millis)
  worker ! FibonacciNumber(68, 50.millis)
  worker ! FibonacciNumber(53, 50.millis)
  worker ! FibonacciNumber(45, 50.millis)

  StdIn.readLine()
  system.terminate()
}
