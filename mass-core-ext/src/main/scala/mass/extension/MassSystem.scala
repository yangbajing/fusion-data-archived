package mass.extension

import akka.actor.typed.ActorSystem
import fusion.common.extension.{ FusionExtension, FusionExtensionId }
import helloscala.common.Configuration
import mass.db.slick.SqlSystem

final class MassSystem private (val system: ActorSystem[_]) extends FusionExtension {
  val core: MassCore = MassCore(system)
  val sqlManager: SqlSystem = SqlSystem(system)

  def connection: Configuration = core.configuration

  override def toString = s"MassSystem($system)"
}

object MassSystem extends FusionExtensionId[MassSystem] {
  override def createExtension(system: ActorSystem[_]): MassSystem = new MassSystem(system)
}
