package mass.scheduler.boot

import mass.core.MassSystem
import mass.scheduler.SchedulerSystem
import mass.scheduler.web.WebServer
import mass.server.MassSystemExtension

object SchedulerBoot extends App {
  val massSystem = MassSystem().as[MassSystemExtension]
  val schedulerSystem = SchedulerSystem(massSystem)
  new WebServer(schedulerSystem).startServerAwait()
}
