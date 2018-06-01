package sample.cluster.sample

import java.util.concurrent.TimeUnit

import akka.actor.Actor
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext.Implicits
import scala.concurrent.Future

class SimpleClusterListener extends Actor with StrictLogging {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def receive: Receive = {
    case MemberUp(member) =>
      logger.info(s"${cluster.selfAddress} 节点已可使用：$member")
    case UnreachableMember(member) =>
      logger.warn(s"${cluster.selfAddress} 节点分离不可达：$member")
    case MemberRemoved(member, previousStatus) =>
      logger.info(s"${cluster.selfAddress} 成员已移除：$member，之前状态：$previousStatus")
    case me: MemberEvent =>
      logger.debug(s"${cluster.selfAddress} ignore event: $me")
    case "Leave" =>
      cluster.leave(cluster.selfAddress)
      Future {
        TimeUnit.SECONDS.sleep(3)
      }(Implicits.global)
  }

}
