package mass.db.slick

import fusion.inject.guice.testkit.GuiceApplicationTestkit
import fusion.jdbc.FusionJdbc
import org.scalatest.wordspec.AnyWordSpecLike

class PgProfileTest extends GuiceApplicationTestkit with AnyWordSpecLike {
  private val profile = injectInstance[PgProfile]
  import profile.api._
  private val db = databaseForDataSource(FusionJdbc(classicSystem).component)

  "test" in {
    val q = sql"select key from job_schedule".as[String]
    println("q.head: " + q.head)
    val result = db.run(q).futureValue
    println(result)
  }

  protected override def afterAll(): Unit = {
    db.close()
    super.afterAll()
  }
}
