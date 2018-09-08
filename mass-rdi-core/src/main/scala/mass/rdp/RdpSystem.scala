package mass.rdp

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import mass.connector.ConnectorSystem
import mass.core.Constants
import mass.extension.MassCore
import mass.rdp.etl.graph.{EtlGraphParserFactory, EtlStreamFactory}
import mass.rdp.module.RdpModule

trait RdpRefFactory {
  def settings: MassCore

  def connectorSystem: ConnectorSystem
}

private[rdp] class RdpSetup(val system: ActorSystem) extends StrictLogging {

  val settings = MassCore(system)

  val extensions: Vector[RdpModule] =
    settings.configuration
      .get[Seq[String]](s"${Constants.BASE_CONF}.rdp.extensions")
      .flatMap { className =>
        Class.forName(className).newInstance() match {
          case v: RdpModule => Some(v)
          case unknown =>
            logger.warn(s"初始化找到未知RdpExtension: $unknown")
            None
        }
      }
      .toVector

  def initialStreamFactories(): Map[String, EtlStreamFactory] = {
    val list = extensions.flatMap(_.etlStreamBuilders) ++
      settings.configuration
        .get[Seq[String]](s"${Constants.BASE_CONF}.rdp.stream-builders")
        .flatMap { className =>
          Class.forName(className).newInstance() match {
            case v: EtlStreamFactory => Some(v)
            case unknown =>
              logger.warn(s"初始化找到未知EtlStreamBuilder: $unknown")
              None
          }
        }
    list.map(v => v.`type` -> v).toMap
  }

  def initialGraphParserFactories(): Map[String, EtlGraphParserFactory] =
    extensions.flatMap(_.graphParserFactories).map(v => v.`type` -> v).toMap

}

/**
 * RDP 系统，保存RDP运行所全局需要的各配置、资源
 */
final class RdpSystem private (val system: ActorSystem, setup: RdpSetup)
    extends RdpRefFactory
    with Extension
    with StrictLogging {
  override val connectorSystem = ConnectorSystem(system)

  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)

  protected var _streamFactories: Map[String, EtlStreamFactory] = setup.initialStreamFactories()

  protected var _graphParerFactories: Map[String, EtlGraphParserFactory] = setup.initialGraphParserFactories()

  def streamFactories: Map[String, EtlStreamFactory] = _streamFactories

  def registerSourceBuilder(b: EtlStreamFactory): Unit = {
    logger.info(s"注册EtlSourceBuilder: $b")
    _streamFactories = _streamFactories.updated(b.`type`, b)
  }

  def graphParserFactories: Map[String, EtlGraphParserFactory] =
    _graphParerFactories

  def registerGraphParserFactories(b: EtlGraphParserFactory): Unit = {
    logger.info(s"注册EtlGraphParserFactor: $b")
    _graphParerFactories = _graphParerFactories.updated(b.`type`, b)
  }
  override def settings: MassCore = setup.settings
  def name: String = system.name
  def configuration: Configuration = settings.configuration
}

object RdpSystem extends ExtensionId[RdpSystem] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): RdpSystem = new RdpSystem(system, new RdpSetup(system))
  override def lookup(): ExtensionId[_ <: Extension] = RdpSystem
}
