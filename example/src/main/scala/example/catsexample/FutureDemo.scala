package example.catsexample

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

import cats.implicits._
import scala.concurrent.duration._

object FutureDemo extends App {

  val f = for {
    _ <- Future { Thread.sleep(1000) }
    (v2, v3) <- Future { 2; Thread.sleep(1000) } product Future { 3; Thread.sleep(1000) }
//    v2 <- Future { Thread.sleep(1000) }
//    v3 <- Future { Thread.sleep(1000) }
  } yield (v2, v3)
  val begin = System.currentTimeMillis()
  val r = Await.result(f, 10.seconds)
  val end = System.currentTimeMillis()
  println(s"${end - begin}ms result: $r")

}
