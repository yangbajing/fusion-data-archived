package mass.extension

import java.nio.file.Path

import akka.actor.typed.ActorSystem
import fusion.common.extension.{ FusionExtension, FusionExtensionId }
import helloscala.common.Configuration
import mass.MassSettings
import mass.db.slick.SqlManager

final class MassSystem private (val system: ActorSystem[_]) extends FusionExtension {
  val core: MassCore = MassCore(system)

  val settings = MassSettings(core.configuration)

  val sqlManager: SqlManager = {
    val v = SqlManager(system)
    sys.addShutdownHook(v.slickDatabase.close())
    v
  }

  def connection: Configuration = core.configuration

  def tempDirectory: Path = core.tempDirectory

  override def toString = s"MassSystem($system)"
}

object MassSystem extends FusionExtensionId[MassSystem] {
  override def createExtension(system: ActorSystem[_]): MassSystem = new MassSystem(system)
}
