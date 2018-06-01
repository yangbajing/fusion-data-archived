package sample.multinode

import akka.actor.{Actor, Props}
import akka.remote.testkit.{MultiNodeConfig, MultiNodeSpec}
import akka.testkit.ImplicitSender

object MultiNodeSampleConfig extends MultiNodeConfig {
  val node1 = role("node1")
  val node2 = role("node2")
}

class MultiNodeSampleTestMultiJvmNode1 extends MultiNodeSampleTest

class MultiNodeSampleTestMultiJvmNode2 extends MultiNodeSampleTest

object MultiNodeSampleTest {

  class Ponger extends Actor {
    def receive = {
      case "ping" => sender() ! "pong"
    }
  }

}

class MultiNodeSampleTest
  extends MultiNodeSpec(MultiNodeSampleConfig)
    with STMultiNodeSpec
    with ImplicitSender {

  import MultiNodeSampleConfig._
  import MultiNodeSampleTest._

  override def initialParticipants: Int = roles.size

  "A MultiNodeSample" must {

    "wait for all nodes to enter a barrier" in {
      enterBarrier("startup") // 当所有节点都发起栅栏消息：startup 后再继续之后代码的运行
    }

    "send to and receive from a remote node" in {
      runOn(node1) {
        enterBarrier("deployed") // 等待另一个节点也发起栅栏 deployed
        val ponger = system.actorSelection(node(node2) / "user" / "ponger")
        ponger ! "ping"
        import scala.concurrent.duration._
        expectMsg(10.seconds, "pong")
        println(System.getProperty("akka.remote.port") + "  received pong")
      }

      runOn(node2) {
        system.actorOf(Props[Ponger], "ponger")
        enterBarrier("deployed")
        println(System.getProperty("akka.remote.port") + "  started ponger")
      }

      enterBarrier("finished")
    }
  }

}
