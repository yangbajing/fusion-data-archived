package fusion.http.server

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller

import scala.reflect.ClassTag

trait JacksonDirectives {
  def jacksonAs[T: ClassTag]: FromRequestUnmarshaller[T] = {
    import fusion.json.jackson.http.JacksonSupport._
    as[T]
  }
}

object JacksonDirectives extends JacksonDirectives {}
