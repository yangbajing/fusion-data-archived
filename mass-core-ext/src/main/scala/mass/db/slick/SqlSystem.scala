package mass.db.slick

import akka.Done
import akka.actor.typed.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.HikariDataSource
import fusion.common.extension.{ FusionExtension, FusionExtensionId }
import fusion.core.extension.FusionCore
import fusion.jdbc.{ FusionJdbc, JdbcTemplate }
import slick.basic.DatabasePublisher

import scala.concurrent.Future
import scala.util.Failure

/**
 * Mass系统SQL数据访问管理器
 */
class SqlSystem private (val system: ActorSystem[_]) extends FusionExtension with StrictLogging {
  import SlickProfile.api._

  val profile: SlickProfile.type = SlickProfile
  val dataSource: HikariDataSource = FusionJdbc(system).component
  val slickDb: SlickProfile.backend.DatabaseDef = databaseForDataSource(dataSource)
  val jdbcTemplate: JdbcTemplate = JdbcTemplate(dataSource)
  FusionCore(system).shutdowns.beforeActorSystemTerminate("StopSqlManager") { () =>
    Future {
      slickDb.close()
      Done
    }(system.executionContext)
  }

  def runTransaction[R, E <: Effect.Write](a: DBIOAction[R, NoStream, E]): Future[R] =
    wrapperLogging(slickDb.run(a.transactionally))

  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = wrapperLogging(slickDb.run(a))

  def stream[T](a: DBIOAction[_, Streaming[T], Nothing]): DatabasePublisher[T] = slickDb.stream(a)

  def streamTransaction[T, E <: Effect.Write](a: DBIOAction[_, Streaming[T], E]): DatabasePublisher[T] =
    slickDb.stream(a.transactionally)

  @inline private def wrapperLogging[T](f: Future[T]): Future[T] =
    f.andThen { case Failure(e) => logger.warn(s"Slick run error [${e.toString}].") }(slickDb.ioExecutionContext)

  override def toString = s"SqlManager($dataSource, $jdbcTemplate, $slickDb)"
}

object SqlSystem extends FusionExtensionId[SqlSystem] {
  override def createExtension(system: ActorSystem[_]): SqlSystem = new SqlSystem(system)
}
