package mass.slick

import java.util.concurrent.TimeUnit

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import fusion.jdbc.FusionJdbc
import mass.slick.SlickProfile.api._
import org.scalatest.wordspec.AnyWordSpecLike
import slick.sql.SqlStreamingAction

class SlickProfileTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  val db = databaseForDataSource(FusionJdbc(system).component)

  "test" in {
    val q: SqlStreamingAction[Vector[String], String, Effect] = sql"select key from job_item".as[String]
    q.head

    TimeUnit.SECONDS.sleep(1)
  }

  protected override def afterAll(): Unit = {
    db.close()
    super.afterAll()
  }
}
