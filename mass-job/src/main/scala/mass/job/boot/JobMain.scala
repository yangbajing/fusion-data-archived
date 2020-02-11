package mass.job.boot

import com.typesafe.config.ConfigFactory
import mass.Global
import mass.job.JobSystem

object JobMain {
  def main(args: Array[String]): Unit = {
    val system = Global.registerActorSystem(ConfigFactory.load())
    val jobSystem = JobSystem(system)
    new JobServer(jobSystem).startServerAwait()
  }
}
