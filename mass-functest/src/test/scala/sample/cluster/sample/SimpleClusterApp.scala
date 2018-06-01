package sample.cluster.sample

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.{ExecutionContext, Future}

object SimpleClusterApp extends App {
  var tm = 0
  Seq("2551", "2552", "0").foreach { port =>
    // Override the configuration of the port
    val config = ConfigFactory.parseString(
      s"""
        akka.remote.netty.tcp.port=$port
        akka.remote.artery.canonical.port=$port
        """).withFallback(ConfigFactory.load("simple-cluster"))

    // Create an Akka system
    val system = ActorSystem("ClusterSystem", config)
    // Create an actor that handles cluster domain events
    val simpleClusterListener = system.actorOf(Props[SimpleClusterListener], name = "clusterListener")

    tm += 5
    Future {
      TimeUnit.SECONDS.sleep(7 + tm)
      simpleClusterListener ! "Leave"
    }(ExecutionContext.Implicits.global)
  }

}
