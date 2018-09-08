package mass.job.boot

import com.typesafe.config.ConfigFactory
import mass.Global
import mass.job.JobSystem

object JobMain extends App {
  val system = Global.registerActorSystem(ConfigFactory.load())
  val jobSystem = JobSystem(system)
  new JobServer(jobSystem).startServerAwait()
}
