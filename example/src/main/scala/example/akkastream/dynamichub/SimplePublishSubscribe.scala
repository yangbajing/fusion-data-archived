package example.akkastream.dynamichub

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, KillSwitches, UniqueKillSwitch}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub, Sink, Source}
import com.typesafe.scalalogging.StrictLogging

import scala.io.StdIn
import scala.concurrent.duration._

object SimplePublishSubscribe extends App with StrictLogging {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  import system.dispatcher

  val (sink, source) = MergeHub
    .source[String](perProducerBufferSize = 16)
    .toMat(BroadcastHub.sink(bufferSize = 256))(Keep.both)
    .run()

  source.runWith(Sink.ignore)

  val busFlow: Flow[String, String, UniqueKillSwitch] = Flow
    .fromSinkAndSource(sink, source)
    .joinMat(KillSwitches.singleBidi[String, String])(Keep.right)
    .backpressureTimeout(3.seconds)

  val switch: UniqueKillSwitch = Source
    .repeat("Hello world!")
    .viaMat(busFlow)(Keep.right)
    .to(Sink.foreach(v => logger.info(s"switch: $v")))
    .run()

  Thread.sleep(200)
  switch.shutdown()

  StdIn.readLine()
  system.terminate()
}
