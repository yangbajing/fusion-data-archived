package example.catsexample

import cats.data.OptionT
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object OptionTDemo {
  val customGreeting: Future[Option[String]] = Future.successful(Some("welcome back, Lola"))

  val customGreetingT: OptionT[Future, String] = OptionT(customGreeting)

  val excitedGreeting: OptionT[Future, String] = customGreetingT.map(_ + "!")

  val withWelcome: OptionT[Future, String] = customGreetingT.filter(_.contains("welcome"))

  val noWelcome: OptionT[Future, String] = customGreetingT.filterNot(_.contains("welcome"))

  val withFallback: Future[String] = customGreetingT.getOrElse("hello, there!")

  val greetingFO: Future[Option[String]] = Future.successful(Some("Hello"))

  val firstnameF: Future[String] = Future.successful("Jane")

  val lastnameO: Option[String] = Some("Doe")

  val ot: OptionT[Future, String] = for {
    g <- OptionT(greetingFO)
    f <- OptionT.liftF(firstnameF)
    l <- OptionT.fromOption[Future](lastnameO)
  } yield s"$g $f $l"

  def main(args: Array[String]) {
    val result: Future[Option[String]] = ot.value // Future(Some("Hello Jane Doe"))
    println(Await.result(result, 10.seconds))
  }

}
