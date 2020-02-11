package mass.slick

import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.HikariDataSource
import fusion.jdbc.JdbcTemplate
import fusion.jdbc.util.JdbcUtils
import helloscala.common.Configuration
import mass.core.Constants
import slick.basic.DatabasePublisher

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Failure

/**
 * Mass系统SQL数据访问管理器
 * @param config 配置
 */
class SqlManager private (config: Configuration) extends StrictLogging {
  import SlickProfile.api._

  val profile: SlickProfile.type = SlickProfile
  val dataSource: HikariDataSource = JdbcUtils.createHikariDataSource(config)
  val slickDatabase: SlickProfile.backend.DatabaseDef = createDatabase(dataSource, config)
  val jdbcTemplate: JdbcTemplate = JdbcTemplate(dataSource, config)

  implicit final def executionContext: ExecutionContext = slickDatabase.ioExecutionContext

  def runTransaction[R, E <: Effect.Write](a: DBIOAction[R, NoStream, E]): Future[R] =
    wrapperLogging(slickDatabase.run(a.transactionally))

  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = wrapperLogging(slickDatabase.run(a))

  def stream[T](a: DBIOAction[_, Streaming[T], Nothing]): DatabasePublisher[T] = slickDatabase.stream(a)

  def streamTransaction[T, E <: Effect.Write](a: DBIOAction[_, Streaming[T], E]): DatabasePublisher[T] =
    slickDatabase.stream(a.transactionally)

  @inline private def wrapperLogging[T](f: Future[T]): Future[T] = {
    f.onComplete {
      case Failure(e) => logger.error(s"Slick future error：${e.getMessage}", e)
      case _          => // do nothing
    }(slickDatabase.ioExecutionContext)
    f
  }

  override def toString = s"SqlManager($dataSource, $jdbcTemplate, $slickDatabase)"
}

object SqlManager {
  val DEFAULT_PATH = s"${Constants.BASE_CONF}.core.persistence.postgres"

  def apply(configuration: Configuration, path: String = DEFAULT_PATH): SqlManager =
    new SqlManager(configuration.getConfiguration(path))
}
