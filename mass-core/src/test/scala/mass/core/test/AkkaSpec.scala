package mass.core.test

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import helloscala.common.test.HelloscalaSpec
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.Await
import scala.concurrent.duration._

trait AkkaSpec extends BeforeAndAfterAll {
  this: HelloscalaSpec =>

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  override protected def afterAll(): Unit = {
    import scala.concurrent.ExecutionContext.Implicits.global
    Await.ready(system.terminate(), 1.minute).failed.foreach(e => println(s"Exit ActorSystem error: ${e.getMessage}"))
    super.afterAll()
  }
}
