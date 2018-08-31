package mass.slick

import com.github.tminglei.slickpg._
import helloscala.common.Configuration
import mass.core.jdbc.JdbcUtils
import slick.basic.Capability
import slick.jdbc.JdbcCapabilities
import slick.util.AsyncExecutor

trait SlickProfile
    extends ExPostgresProfile
    with PgArraySupport
    with PgDate2Support
    with PgHStoreSupport
    with PgJacksonSupport
    with MassSlickSupport {

  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val pgjson = "jsonb"

  val plainApi = new PlainAPI {}
  override val api: API = new API {}

  trait PlainAPI
      extends ByteaPlainImplicits
      with SimpleArrayPlainImplicits
      with JacksonJsonPlainImplicits
      with Date2DateTimePlainImplicits
      with SimpleHStorePlainImplicits
      with MassPlainImplicits {}

  trait API
      extends super.API
      with MassArrayImplicits
      with JsonImplicits
      with DateTimeImplicits
      with HStoreImplicits
      with MassSlickImplicits {}

  trait ColumnOptions extends super.ColumnOptions {
    val SqlTypeObjectId = SqlType("char(24)")
    val SqlTypeSha256 = SqlType("char(64)")
  }

  override val columnOptions: ColumnOptions = new ColumnOptions {}

  def createDatabase(configuration: Configuration): backend.DatabaseDef = {
    val ds = JdbcUtils.createHikariDataSource(configuration)
    val poolName = configuration.getOrElse[String]("poolName", "default")
    val numThreads = configuration.getOrElse[Int]("numThreads", 20)
    val maximumPoolSize = configuration.getOrElse[Int]("maximumPoolSize", numThreads)
    val registerMbeans = configuration.getOrElse[Boolean]("registerMbeans", false)
    val executor = AsyncExecutor(poolName,
                                 numThreads,
                                 numThreads,
                                 configuration.getOrElse[Int]("queueSize", 1000),
                                 maximumPoolSize,
                                 registerMbeans = registerMbeans)
    api.Database.forDataSource(ds, Some(maximumPoolSize), executor)
  }
}

object SlickProfile extends SlickProfile
