package mass.rdp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import mass.connector.ConnectorSystem
import mass.core.{BaseSystem, MassSystem}
import mass.rdp.etl.graph.{EtlGraphParserFactory, EtlStreamFactory}
import mass.rdp.module.RdpModule

trait RdpRefFactory extends BaseSystem {
  def massSystem: MassSystem

  def connectorSystem: ConnectorSystem
}

private[rdp] class RdpSetup(val massSystem: MassSystem,
                            val connectorSystem: ConnectorSystem)
    extends StrictLogging {

  val extensions: Vector[RdpModule] =
    massSystem.configuration
      .get[Seq[String]]("mass.rdp.extensions")
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
      massSystem.configuration
        .get[Seq[String]]("mass.rdp.stream-builders")
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

  def initialGraphParserFactories(): Map[String, EtlGraphParserFactory] = {
    extensions.flatMap(_.graphParserFactories).map(v => v.`type` -> v).toMap
  }

}

/**
  * RDP 系统，保存RDP运行所全局需要的各配置、资源
  */
abstract class RdpSystem extends RdpRefFactory with StrictLogging {
  implicit def materializer: ActorMaterializer

  protected var _streamFactories: Map[String, EtlStreamFactory]

  protected var _graphParerFactories: Map[String, EtlGraphParserFactory]

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

}

object RdpSystem {
  private var _instance: RdpSystem = _
  def instance: RdpSystem = _instance

  def apply(name: String,
            massSystem: MassSystem,
            connectorSystem: ConnectorSystem): RdpSystem = {
    apply(name, new RdpSetup(massSystem, connectorSystem))
  }

  def apply(name: String, setup: RdpSetup): RdpSystem = {
    _instance = new RdpSystemImpl(name,
                                  setup.massSystem,
                                  setup.connectorSystem,
                                  setup.initialStreamFactories(),
                                  setup.initialGraphParserFactories())
    _instance
  }

}

private[rdp] class RdpSystemImpl(
    val name: String,
    val massSystem: MassSystem,
    val connectorSystem: ConnectorSystem,
    protected var _streamFactories: Map[String, EtlStreamFactory],
    protected var _graphParerFactories: Map[String, EtlGraphParserFactory]
) extends RdpSystem {

  override def system: ActorSystem = massSystem.system

  override def configuration: Configuration = massSystem.configuration

  override implicit def materializer: ActorMaterializer =
    ActorMaterializer()(massSystem.system)

}
