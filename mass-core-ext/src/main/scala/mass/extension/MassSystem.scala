package mass.extension

import akka.actor.ExtendedActorSystem
import fusion.common.extension.{ FusionExtension, FusionExtensionId }
import helloscala.common.Configuration
import mass.db.slick.SqlSystem

final class MassSystem private (override val classicSystem: ExtendedActorSystem) extends FusionExtension {
  val core: MassCore = MassCore(classicSystem)
  val sqlSystem: SqlSystem = SqlSystem(classicSystem)

  def connection: Configuration = core.configuration

  override def toString = s"MassSystem($classicSystem)"
}

object MassSystem extends FusionExtensionId[MassSystem] {
  override def createExtension(system: ExtendedActorSystem): MassSystem = new MassSystem(system)
}
