package mass.job.service

import akka.actor.typed.ActorSystem
import fusion.common.FusionProtocol
import fusion.common.extension.{ FusionExtension, FusionExtensionId }
import mass.extension.MassSystem
import mass.job.service.job.JobService

class Services private (val system: ActorSystem[FusionProtocol.Command]) extends FusionExtension {
  val massSystem = MassSystem(system)
  val jobService = new JobService(system)
}

object Services extends FusionExtensionId[Services] {
  override def createExtension(system: ActorSystem[_]): Services =
    new Services(system.asInstanceOf[ActorSystem[FusionProtocol.Command]])
}
