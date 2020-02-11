package mass.extension

import java.nio.file.Path

import akka.actor.{ ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import helloscala.common.Configuration
import mass.server.MassSettings
import mass.slick.SqlManager

final class MassSystem private (val system: ExtendedActorSystem) extends Extension {
  val core: MassCore = MassCore(system)

  val settings = MassSettings(core.configuration)

  val sqlManager: SqlManager = {
    val v = SqlManager(core.configuration)
    sys.addShutdownHook(v.slickDatabase.close())
    v
  }

  def connection: Configuration = core.configuration

  def tempDirectory: Path = core.tempDirectory

  override def toString = s"MassSystem($system)"
}

object MassSystem extends ExtensionId[MassSystem] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): MassSystem = new MassSystem(system)
  override def lookup(): ExtensionId[_ <: Extension] = MassSystem
}
