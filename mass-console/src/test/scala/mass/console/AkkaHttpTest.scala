package mass.console

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer

import scala.concurrent.Future

case class SpidFacebookData(AppId: String,
                            Type: String,
                            Application: String,
                            ExpiresAt: Long,
                            IsValid: Boolean,
                            IssuedAt: Long,
                            Scopes: Array[String],
                            UserId: String)

class AkkaHttpTest {
  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  import system.dispatcher

  val resultFuture: Future[HttpResponse] = Http().singleRequest(
    HttpRequest(
      HttpMethods.POST,
      "/api/signin",
      headers = List(headers.`Content-Type`(ContentTypes.`application/json`)),
      entity =
        """{"account":"yangbajing@gmail.com", "password": "yangbajing"}"""
    ))

  resultFuture
    .flatMap(httpResponse => Unmarshal(httpResponse.entity).to[String])
    .foreach { str =>
      println(str)
    }

}
