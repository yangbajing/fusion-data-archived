package mass.scheduler.boot

import mass.core.MassSystem
import mass.scheduler.SchedulerSystem
import mass.server.MassSystemExtension

object SchedulerBoot extends {
  MassSystem()
  SchedulerSystem(MassSystemExtension.instance)
}
