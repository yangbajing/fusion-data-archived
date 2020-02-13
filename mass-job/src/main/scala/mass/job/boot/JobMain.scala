package mass.job.boot

import com.typesafe.config.ConfigFactory
import mass.Mass
import mass.job.JobSystem

object JobMain {
  def main(args: Array[String]): Unit = {
    val jobSystem = JobSystem(Mass.fromConfig(ConfigFactory.load()).system)
    new JobServer(jobSystem).startServer()
  }
}
