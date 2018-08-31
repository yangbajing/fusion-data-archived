/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package mass.server

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

trait MetricActor extends Actor with LazyLogging {

  override def preStart(): Unit =
    logger.info(s"actor已启动: $self")

  override def postStop(): Unit =
    logger.info(s"actor已停止：$self")

  //  override def preRestart(reason: Throwable, message: Option[Any]): Unit = super.preRestart(reason, message)
  //
  //  override def postRestart(reason: Throwable): Unit = super.postRestart(reason)

  final override def receive: Receive = metricReceive

  def metricReceive: Receive
}
