package mass.db.slick

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import fusion.jdbc.FusionJdbc
import mass.db.slick.PgProfile.api._
import org.scalatest.wordspec.AnyWordSpecLike
import slick.sql.SqlStreamingAction

class PgProfileTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  private val db = databaseForDataSource(FusionJdbc(system).component)

  "test" in {
    val q: SqlStreamingAction[Vector[String], String, Effect] = sql"select key from job_schedule".as[String]
    println("q.head: " + q.head)
    val result = db.run(q).futureValue
    println(result)
  }

  protected override def afterAll(): Unit = {
    db.close()
    super.afterAll()
  }
}
