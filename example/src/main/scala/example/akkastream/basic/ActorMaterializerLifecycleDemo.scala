package example.akkastream.basic

import akka.actor.{ Actor, ActorSystem, Props }
import akka.stream.{ ActorMaterializer, Materializer }
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source

import scala.io.StdIn
import scala.util.{ Failure, Success }

/**
 * ActorMaterializer由actor上下文创建，actor退出则流退出。
 */
class RunWithItself extends Actor {
  implicit val mat = ActorMaterializer()

  Source.maybe.runWith(Sink.onComplete {
    case Success(done) => println(s"$self Complated: $done")
    case Failure(e)    => println(s"$self Failed: ${e.getMessage}")
  })

  override def receive: Receive = {
    case "boom" => context.stop(self)
  }
}

class RunForever(implicit val mat: Materializer) extends Actor {
  Source.maybe.runWith(Sink.onComplete {
    case Success(done) => println(s"$self Complated: $done")
    case Failure(e)    => println(s"$self Failed: ${e.getMessage}")
  })

  override def receive: Receive = {
    case "boom" => context.stop(self)
  }
}

object ActorMaterializerLifecycleDemo extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  system.actorOf(Props[RunWithItself], "with-itself") ! "boom"
  val runForever = system.actorOf(Props(new RunForever), "run-forever")
  //  Thread.sleep(100)
  //  mat.shutdown()
  //  Thread.sleep(200)
  runForever ! "boom"

  StdIn.readLine()
  system.terminate()
}
