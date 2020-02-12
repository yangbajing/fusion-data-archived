package sample

import akka.actor.{Actor, Props}
import akka.remote.testconductor.RoleName
import akka.remote.testkit.{MultiNodeConfig, MultiNodeSpec}
import akka.testkit.ImplicitSender
import mass.STMultiNodeSpec

object MultiNodeSampleConfig extends MultiNodeConfig {
  val node1: RoleName = role("node1")
  val node2: RoleName = role("node2")
}

object MultiNodeSampleSpec {

  class Ponger extends Actor {
    def receive: Receive = {
      case "ping" => sender() ! "pong"
    }
  }

}

class MultiNodeSampleSpec extends MultiNodeSpec(MultiNodeSampleConfig) with STMultiNodeSpec with ImplicitSender {

  import MultiNodeSampleSpec._
  import MultiNodeSampleConfig._

  // 设置参与者数量，之后的Barrier（enterBarrier）需要满足此数量后才运行之后的代码。
  def initialParticipants: Int = roles.size

  "A MultiNodeSampleSpec" must {

    "wait for all nodes to enter a barrier" in {
      enterBarrier("startup")
    }

    "send to and receive from a remote node" in {
      runOn(node1) {
        // 进入 deployed barrier，等待另一个节点实例化 actor 完成。
        enterBarrier("deployed")
        val ponger = system.actorSelection(node(node2) / "user" / "ponger")
        ponger ! "ping"
        import scala.concurrent.duration._
        expectMsg(10.seconds, "pong") // 阻塞接收并assert消息，10秒超时
      }

      runOn(node2) {
        system.actorOf(Props[Ponger], "ponger")
        // 先实例化actor，再进入 deployed barrier
        enterBarrier("deployed")
      }

      enterBarrier("finished")
    }
  }
}

class MultiNodeSampleSpecMultiJvmNode1 extends MultiNodeSampleSpec
class MultiNodeSampleSpecMultiJvmNode2 extends MultiNodeSampleSpec
