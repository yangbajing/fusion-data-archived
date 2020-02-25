package mass.job

import fusion.core.FusionApplication
import fusion.http.FusionHttpServer
import fusion.inject.GuiceApplication
import mass.job.route.Routes

object JobMain {
  def main(args: Array[String]): Unit = {
    val application = FusionApplication.start().asInstanceOf[GuiceApplication]
    FusionHttpServer(application.classicSystem).component.startBaseRouteSync(application.instance[Routes])
  }
}
