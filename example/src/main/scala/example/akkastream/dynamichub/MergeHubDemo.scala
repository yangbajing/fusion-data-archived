package example.akkastream.dynamichub

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{MergeHub, RunnableGraph, Sink, Source}
import com.typesafe.scalalogging.StrictLogging

import scala.io.StdIn

object MergeHubDemo extends App with StrictLogging {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  // A simple consumer that will print to the console for now
  val consumer = Sink.foreach[String](v => logger.info(s"consumer: $v"))

  // Attach a MergeHub Source to the consumer. This will materialize to a
  // corresponding Sink.
  val runnableGraph: RunnableGraph[Sink[String, NotUsed]] =
    MergeHub.source[String](perProducerBufferSize = 16).to(consumer)

  // By running/materializing the consumer we get back a Sink, and hence
  // now have access to feed elements into it. This Sink can be materialized
  // any number of times, and every element that enters the Sink will
  // be consumed by our consumer.
  val toConsumer: Sink[String, NotUsed] = runnableGraph.run()

  // Feeding two independent sources into the hub.
  Source.single("Hello!").runWith(toConsumer)
  Source.single("Hub!").runWith(toConsumer)

  StdIn.readLine()
  system.terminate()
}

