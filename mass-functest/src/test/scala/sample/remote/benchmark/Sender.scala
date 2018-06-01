package sample.remote.benchmark

import akka.actor.{Actor, ActorIdentity, ActorRef, ActorSystem, Identify, Props, ReceiveTimeout, Terminated}
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

class Sender(path: String, totalMessages: Int, burstSize: Int, payloadSize: Int) extends Actor {
  import Sender._
  val payload: Array[Byte] = Vector.fill(payloadSize)("a").mkString.getBytes
  println(s"payload bytes: ${payload.length}")
  var startTime = 0L
  var maxRoundTripMillis = 0L

  context.setReceiveTimeout(3.seconds) // 设置Actor自身接收消息超时时长
  sendIdentifyRequest() // 发请求确认远程actor的路径是否有效。

  override def receive: Receive = identifying

  def identifying: Receive = {
    case ActorIdentity(`path`, Some(actor)) =>
      context.watch(actor)
      context.become(active(actor))
      context.setReceiveTimeout(Duration.Undefined) // 重置超时时间
      self ! Warmup

    case ActorIdentity(`path`, None) =>
      println(s"远程actor无效：$path")

    case ReceiveTimeout =>
      sendIdentifyRequest() // 超时，再次确认远程actor是否有效
  }

  def active(actor: ActorRef): Receive = {
    case Warmup => // 热身，不计入统计
      sendBatch(actor, burstSize)
      actor ! Start

    case Start =>
      println(s"启动基准测试一共 $totalMessages 消息，分帧大小 $burstSize，有效负载 $payloadSize")
      startTime = System.nanoTime()
      val remaining = sendBatch(actor, totalMessages)
      if (remaining == 0)
        actor ! Done
      else
        actor ! Continue(remaining, startTime, startTime, burstSize)

    case c @ Continue(remaining, t0, t1, n) =>
      val now = System.nanoTime()
      val duration = (now - t0).nanos.toMillis // 从发出 Continue 指令到收到指令回复花费的时间
      val roundTripMillis = (now - t1).nanos.toMillis
      maxRoundTripMillis = math.max(maxRoundTripMillis, roundTripMillis)
      if (duration >= 500) { // 以500ms为间隔作统计
        val throughtput = (n * 1000.0 / duration).toInt
        println(s"花费 ${duration}ms 发送了 $n 条消息，吞吐量 ${throughtput}msg/s，")
      }

      val nextRemaining = sendBatch(actor, remaining)
      if (nextRemaining == 0)
        actor ! Done
      else if (duration >= 500) // 一个批次的数量已发完
        actor ! Continue(nextRemaining, now, now, burstSize)
      else // 间隔时间不足500ms，更新 剩余数量、（分帧）起始时间、分帧发送数量
        actor ! c.copy(remaining = nextRemaining, burstStartTime = now, n = n + burstSize)

    case Done =>
      val took = (System.nanoTime - startTime).nanos.toMillis
      val throughtput = (totalMessages * 1000.0 / took).toInt
      println(s"一共花费 ${took}ms 发送了 ${totalMessages}消息, 吞吐量 ${throughtput}msg/s, " +
        s"最大往返时间 ${maxRoundTripMillis}ms, 分帧数据大小 $burstSize, " +
        s"有效负载 $payloadSize")
      actor ! Shutdown

    case Terminated(`actor`) =>
      println("Receiver terminated")
      context.system.terminate()
  }

  /**
   *
   * @param actor 无端actor
   * @param remaining 还剩多少数据量未发送
   * @return 剩余未发送数据量
   */
  private def sendBatch(actor: ActorRef, remaining: Int): Int = {
    // 取实际的发送消息数，从预设的burstSize和传入的remaining中取最小的一个
    val batchSize = math.min(remaining, burstSize)

    (1 to batchSize).foreach(_ => actor ! payload) // 发送的实际消息数据
    remaining - batchSize
  }

  private def sendIdentifyRequest(): Unit = {
    // 通过发送 path 消息在收到回复消息时来根据 actor path 来区分不同的远程actor
    context.actorSelection(path) ! Identify(path)
  }
}

object Sender {
  private case object Warmup
  case object Shutdown

  /**
   * 控制指令
   */
  sealed trait Echo
  case object Start extends Echo
  case object Done extends Echo

  /**
   *
   * @param remaining 容量
   * @param startTime 开始时间
   * @param burstStartTime （塞满）时间
   * @param n （塞满）数量
   */
  case class Continue(remaining: Int, startTime: Long, burstStartTime: Long, n: Int) extends Echo

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Sys", ConfigFactory.load("calculator"))
    val remoteHostPort = if (args.nonEmpty) args(0) else "127.0.0.1:2553"
    val remotePath = s"akka.tcp://Sys@$remoteHostPort/user/rcv"
    val totalMessages = if (args.length >= 2) args(1).toInt else 500000
    val burstSize = if (args.length >= 3) args(2).toInt else 5000
    val payloadSize = if (args.length >= 4) args(3).toInt else 100

    system.actorOf(Sender.props(remotePath, totalMessages, burstSize, payloadSize), "snd")
  }

  def props(path: String, totalMessages: Int, burstSize: Int, payloadSize: Int) =
    Props(new Sender(path, totalMessages, burstSize, payloadSize))

}
