/**
 * http://blog.colinbreck.com/akka-streams-a-motivating-example/
 */
package example.motivating

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import example.motivating.DatabaseActor.InsertMessage

import scala.concurrent.Future
import scala.concurrent.duration._
import akka.http.scaladsl.server.Directives._

object StreamDemo extends App {
  implicit val system: ActorSystem = ActorSystem("database-actor")
  implicit val mat: ActorMaterializer = ActorMaterializer()

  import system.dispatcher

  val database = new Database()

  val measurementsWebSocketService =
    Flow[Message]
      .collect {
        case TextMessage.Strict(text)         => Future.successful(text)
        case TextMessage.Streamed(textStream) => textStream.runFold("")(_ + _).flatMap(Future.successful)
      }
      .mapAsync(1)(identity)
      .map(InsertMessage.parse)
      .groupedWithin(1000, 1.second)
      .mapAsync(10)(messages => database.bulkInsertAsync(messages.map(_.message)))
      .map(messages => InsertMessage.ack(messages.last))

  val route = path("measurements") {
    get {
      handleWebSocketMessages(measurementsWebSocketService)
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
}
