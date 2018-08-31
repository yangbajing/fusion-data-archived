package mass.scheduler.boot

import mass.core.MassSystem
import mass.scheduler.SchedulerSystem
import mass.server.MassSystemExtension

object SchedulerBoot extends App {
  val massSystem = MassSystem().as[MassSystemExtension]
  val schedulerSystem = SchedulerSystem(massSystem)
  new SchedulerServer(schedulerSystem).startServerAwait()
}
