package mass.core.test

import java.nio.file.Files
import java.time.OffsetDateTime

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import helloscala.common.test.HelloscalaSpec
import helloscala.common.util.TimeUtils
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

class DemoTest extends HelloscalaSpec {

  "temp file" in {
    val p = Files.createTempFile("mass-", ".tmp")
    println(p)
  }

  "offset-datetime" in {
    val odt = OffsetDateTime.now()
    println(odt)
    val str = odt.toString
    println(TimeUtils.toOffsetDateTime(str))
  }

}
