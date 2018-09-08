/**
 * http://blog.colinbreck.com/patterns-for-streaming-measurement-data-with-akka-streams/
 */
package example.motivating

import java.security.SecureRandom
import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.jackson.Jackson
import mass.core.job.SchedulerJob
import mass.model.job.JobItem
import mass.session.AuthSession

import scala.concurrent.duration._
import scala.io.StdIn

case class Status()

case class Sample(timestamp: Long, sample: Float)
