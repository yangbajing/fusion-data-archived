package mass.slick

import akka.actor.typed.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.HikariDataSource
import fusion.jdbc.{ FusionJdbc, JdbcTemplate }
import mass.core.Constants
import slick.basic.DatabasePublisher

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Failure

/**
 * Mass系统SQL数据访问管理器
 */
class SqlManager private (system: ActorSystem[_]) extends StrictLogging {
  import SlickProfile.api._

  val profile: SlickProfile.type = SlickProfile
  val dataSource: HikariDataSource = FusionJdbc(system).component
  val slickDatabase: SlickProfile.backend.DatabaseDef = databaseForDataSource(dataSource)
  val jdbcTemplate: JdbcTemplate = JdbcTemplate(dataSource)

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

  def apply(system: ActorSystem[_]): SqlManager =
    new SqlManager(system)
}
