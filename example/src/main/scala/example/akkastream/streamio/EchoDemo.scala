package example.akkastream.streamio

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Framing, Sink, Source, Tcp }
import akka.util.ByteString
import example.akkastream.streamio.EchoServer.system

import scala.concurrent.Future
import scala.io.StdIn

object EchoServer extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val connections = Tcp().bind("localhost", 8888)
  connections.runForeach { connection =>
    println(s"New connection from: ${connection.remoteAddress}")

    val echo: Flow[ByteString, ByteString, NotUsed] = Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), 256, true))
      .map(_.utf8String)
      .map(_ + "!!!\n")
      .map(ByteString(_))

    connection.handleWith(echo)
  }

  StdIn.readLine()
  system.terminate()
}

object EchoClient extends App {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val connection = Tcp().outgoingConnection("localhost", 8888)

  val replParser =
    Flow[String].takeWhile(_ != "q").concat(Source.single("BYE")).map { elem =>
      println(s"send msg: $elem")
      ByteString(s"$elem\n")
    }

  val repl = Flow[ByteString]
    .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
    .map(_.utf8String)
    .map(text => println("Server: " + text))
    .map(_ => StdIn.readLine("> "))
    .via(replParser)

  val connected: Future[Tcp.OutgoingConnection] = connection.join(repl).run()

  //  StdIn.readLine()
  //  system.terminate()
}

object EchoDemo {}
