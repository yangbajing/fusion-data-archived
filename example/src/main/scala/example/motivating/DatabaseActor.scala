package example.motivating

import java.util.concurrent.TimeUnit

import akka.NotUsed
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import example.motivating.DatabaseActor.InsertMessage

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._

class Database {

  //  def bulkInsertAsync(insert: Seq[InsertMessage])(implicit ec: ExecutionContext): Future[Seq[String]] = bulkInsertAsync(insert.map(_.message))

  def bulkInsertAsync(insert: Seq[String])(implicit ec: ExecutionContext): Future[Seq[String]] = Future {
    TimeUnit.MILLISECONDS.sleep(50)
    insert
  }

}

object DatabaseActor {

  case object Insert

  case object Decrement

  case class InsertMessage(message: String)

  object InsertMessage {
    def parse(message: String): InsertMessage = InsertMessage(message)

    def ack(message: String): TextMessage.Strict = TextMessage("ack-" + message)
  }

}

class DatabaseActor extends Actor {

  import DatabaseActor._
  import context.dispatcher

  val database = new Database()

  var messages: Seq[String] = Nil
  var count = 0
  var flush = true
  var outstanding = 0

  override def preStart() = {
    context.system.scheduler.scheduleOnce(1.second) {
      self ! Insert
    }
  }

  def receive: Receive = {
    case InsertMessage(message) =>
      messages = message +: messages
      count += 1
      if (count >= 1000) {
        insert()
        flush = false
      }
    case Insert =>
      if (flush) insert() else flush = true
      context.system.scheduler.scheduleOnce(1.second) {
        self ! Insert
      }
    case Decrement =>
      outstanding -= 1
      if (count >= 1000) {
        insert()
        flush = false
      }
  }

  private def insert() = {
    if (count > 0 && outstanding < 10) {
      outstanding += 1
      val (insert, remaining) = messages.splitAt(1000)
      messages = remaining
      count = remaining.size
      database.bulkInsertAsync(insert) andThen {
        case _ => self ! Decrement
      }
    }
  }
}

import akka.http.scaladsl.server.Directives._

object DatabaseDemo extends App {
  implicit val system: ActorSystem = ActorSystem("database-actor")
  implicit val mat: ActorMaterializer = ActorMaterializer()

  val database = system.actorOf(Props[DatabaseActor], "database")
  val measurementsWebSocketService: Flow[Message, Message, NotUsed] =
    Flow[Message]
      .collect {
        case TextMessage.Strict(text) =>
          val message = InsertMessage.parse(text)
          database ! message
          InsertMessage.ack(message.message)
      }

  val route = path("measurements") {
    get {
      handleWebSocketMessages(measurementsWebSocketService)
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
}
