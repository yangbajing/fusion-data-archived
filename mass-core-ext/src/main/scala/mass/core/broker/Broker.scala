package mass.core.broker

import akka.actor.{ ActorRef, ActorSystem, PoisonPill, Props }
import akka.cluster.singleton._

trait Broker {
  val system: ActorSystem

  protected var _managerLeader: ActorRef = _

  def managerLeader: ActorRef = {
    require(_managerLeader ne null, "单例Leader未实例化")
    _managerLeader
  }

  protected var _proxyLeader: ActorRef = _

  def proxyLeader: ActorRef = {
    require(_proxyLeader ne null, "单例Leader代理未实例化")
    _proxyLeader
  }

  def init(leaderProps: Props, role: Option[String], name: String, initProxyLeader: Boolean = true): Unit = {
    val settings = ClusterSingletonManagerSettings(system)
    initLeader(leaderProps, role.map(settings.withRole).getOrElse(settings), name)
    if (initProxyLeader) {
      val proxySettings = ClusterSingletonProxySettings(system)
      initBrokerLeaderProxy(name, role.map(proxySettings.withRole).getOrElse(proxySettings), s"$name-proxy")
    }
  }

  protected def initLeader(leaderProps: Props, role: Option[String], name: String): Unit =
    initLeader(leaderProps, ClusterSingletonManagerSettings(system).withRole(role), name)

  protected def initLeader(leaderProps: Props, settings: ClusterSingletonManagerSettings, name: String): Unit = {
    val props =
      ClusterSingletonManager.props(singletonProps = leaderProps, terminationMessage = PoisonPill, settings = settings)
    _managerLeader = system.actorOf(props, name)
  }

  protected def initBrokerLeaderProxy(leaderName: String, role: Option[String], proxyName: String): Unit =
    initBrokerLeaderProxy(leaderName, ClusterSingletonProxySettings(system).withRole(role), proxyName)

  protected def initBrokerLeaderProxy(
      leaderName: String,
      proxySettings: ClusterSingletonProxySettings,
      proxyName: String): Unit = {
    val props = ClusterSingletonProxy.props(s"/user/$leaderName", proxySettings)
    _proxyLeader = system.actorOf(props, proxyName)
  }
}
