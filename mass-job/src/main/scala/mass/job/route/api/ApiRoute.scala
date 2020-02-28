package mass.job.route.api

import akka.http.scaladsl.server.Route
import fusion.http.server.{ BaseRoute, HttpDirectives }
import javax.inject.{ Inject, Singleton }
import mass.job.route.api.v1.JobRoute

@Singleton
class ApiRoute @Inject() (jobRoute: JobRoute) extends BaseRoute with HttpDirectives {
  override def route: Route = pathPrefix("api") {
    pathPrefix("v1") {
      jobRoute.route
    }
  }
}
