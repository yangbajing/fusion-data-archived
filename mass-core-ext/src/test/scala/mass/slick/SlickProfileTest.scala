package mass.slick

import java.util.concurrent.TimeUnit

import fusion.jdbc.util.JdbcUtils
import helloscala.common.Configuration
import mass.core.Constants
import mass.slick.SlickProfile.api._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpec
import slick.sql.SqlStreamingAction

class SlickProfileTest extends AnyWordSpec with BeforeAndAfterAll {
  val configuration = Configuration.load()
  private val postgresProps = configuration.getConfiguration(s"${Constants.BASE_CONF}.core.persistence.postgres")
  val ds = JdbcUtils.createHikariDataSource(postgresProps)
  val db = createDatabase(ds, postgresProps)

  "test" in {
    val q: SqlStreamingAction[Vector[String], String, Effect] = sql"select key from job_item".as[String]
    q.head

    TimeUnit.SECONDS.sleep(1)
  }

  override protected def beforeAll(): Unit = super.beforeAll()
  override protected def afterAll(): Unit = db.close()
}
