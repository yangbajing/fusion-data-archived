package mass.rdp

import akka.actor.ExtendedActorSystem
import akka.stream.Materializer
import com.typesafe.scalalogging.StrictLogging
import fusion.common.extension.{ FusionExtension, FusionExtensionId }
import mass.connector.ConnectorSystem
import mass.core.Constants
import mass.extension.MassCore
import mass.rdp.etl.graph.{ EtlGraphParserFactory, EtlStreamFactory }
import mass.rdp.module.RdpModule

import scala.util.{ Failure, Success }

trait RdpRefFactory {
  def settings: MassCore

  def connectorSystem: ConnectorSystem
}

private[rdp] class RdpSetup(val system: ExtendedActorSystem) extends StrictLogging {
  val massCore = MassCore(system)

  val extensions: Vector[RdpModule] =
    massCore.configuration
      .get[Seq[String]](s"${Constants.BASE_CONF}.rdp.extensions")
      .flatMap { className =>
        system.dynamicAccess.createInstanceFor[RdpModule](className, Nil) match {
          case Success(v) => Some(v)
          case Failure(e) =>
            logger.warn(s"初始化找到未知RdpExtension", e)
            None
        }
      }
      .toVector

  def initialStreamFactories(): Map[String, EtlStreamFactory] = {
    val list = extensions.flatMap(_.etlStreamBuilders) ++
      massCore.configuration.get[Seq[String]](s"${Constants.BASE_CONF}.rdp.stream-builders").flatMap { className =>
        system.dynamicAccess.createInstanceFor[EtlStreamFactory](className, Nil) match {
          case Success(v) => Some(v)
          case Failure(e) =>
            logger.warn(s"初始化找到未知EtlStreamBuilder", e)
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
final class RdpSystem private (override val classicSystem: ExtendedActorSystem)
    extends RdpRefFactory
    with FusionExtension
    with StrictLogging {
  override val connectorSystem: ConnectorSystem = ConnectorSystem(classicSystem)
  implicit val materializer: Materializer = Materializer.matFromSystem(classicSystem)

  private val setup = new RdpSetup(classicSystem)

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
  override def settings: MassCore = setup.massCore
  def name: String = classicSystem.name
}

object RdpSystem extends FusionExtensionId[RdpSystem] {
  override def createExtension(system: ExtendedActorSystem): RdpSystem = new RdpSystem(system)
}
