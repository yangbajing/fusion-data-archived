package mass.job.boot

import com.typesafe.config.ConfigFactory
import kamon.Kamon
import kamon.prometheus.PrometheusReporter
import kamon.zipkin.ZipkinReporter
import mass.Global
import mass.job.JobSystem

object JobMain extends App {
  val system = Global.registerActorSystem(ConfigFactory.load())
  val jobSystem = JobSystem(system)
  new JobServer(jobSystem).startServerAwait()

  Kamon.addReporter(new PrometheusReporter())
  Kamon.addReporter(new ZipkinReporter())
}
