package mass.job

import com.typesafe.config.ConfigFactory
import fusion.http.FusionHttpServer
import mass.Mass
import mass.job.route.Routes

object JobMain {
  def main(args: Array[String]): Unit = {
    val mass = Mass.fromConfig(ConfigFactory.load())
    FusionHttpServer(mass.system).component.startAbstractRouteSync(new Routes(mass))
  }
}
