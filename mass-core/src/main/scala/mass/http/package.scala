package mass

import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.scaladsl.SourceQueueWithComplete

import scala.concurrent.Promise

package object http {
  type HttpSourceQueue =
    SourceQueueWithComplete[(HttpRequest, Promise[HttpResponse])]
}
