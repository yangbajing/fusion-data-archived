package mass.core.broker

import akka.actor.Actor
import akka.cluster.{ Cluster, Member, MemberStatus }
import akka.cluster.ClusterEvent._

trait ClusterActor extends Actor {
  var members: Set[Member] = Set()

  val cluster: Cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.registerOnMemberUp {
      cluster.subscribe(self, classOf[MemberEvent], classOf[UnreachableMember])
    }
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def receive: Receive = {
    case m: MemberEvent             => saveMember(m)
    case state: CurrentClusterState => state.members.filter(_.status == MemberStatus.Up).foreach(saveMember)
  }

  private def saveMember(event: MemberEvent): Unit = event match {
    case MemberUp(m)     => members = members.filter(_.address == m.address) + m
    case MemberExited(m) => members = members.filterNot(_.address == m.address)
  }

  private def saveMember(member: Member): Unit = {
    members = members.filter(_.address == member.address) + member
  }
}
