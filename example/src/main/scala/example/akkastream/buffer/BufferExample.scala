package example.akkastream.buffer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Sink, Source }

import scala.io.StdIn

object BufferExample extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  Source(1 to 3)
    .map { i =>
      println(s"A: $i"); i
    }
    .async
    .map { i =>
      println(s"B: $i"); i
    }
    .async
    .map { i =>
      println(s"C: $i"); i
    }
    .async
    .runWith(Sink.ignore)

  Thread.sleep(1000)
  println("------------------------------------")
  Source(1 to 3)
    .map { i =>
      println(s"A: $i"); i
    }
    .map { i =>
      println(s"B: $i"); i
    }
    .map { i =>
      println(s"C: $i"); i
    }
    .runWith(Sink.ignore)

  StdIn.readLine()
  system.terminate()
}
