package mass.db.slick

import akka.Done
import akka.actor.ExtendedActorSystem
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
class SqlSystem private (override val classicSystem: ExtendedActorSystem) extends FusionExtension with StrictLogging {
  import PgProfile.api._

  val profile: PgProfile = PgProfile
  val dataSource: HikariDataSource = FusionJdbc(classicSystem).component
  val db: PgProfile.backend.DatabaseDef = databaseForDataSource(dataSource)
  val jdbcTemplate: JdbcTemplate = JdbcTemplate(dataSource)
  FusionCore(classicSystem).shutdowns.beforeActorSystemTerminate("StopSqlManager") { () =>
    Future {
      db.close()
      Done
    }(classicSystem.dispatcher)
  }

  def runTransaction[R, E <: Effect.Write](a: DBIOAction[R, NoStream, E]): Future[R] =
    wrapperLogging(db.run(a.transactionally))

  def run[R](a: DBIOAction[R, NoStream, Nothing]): Future[R] = wrapperLogging(db.run(a))

  def stream[T](a: DBIOAction[_, Streaming[T], Nothing]): DatabasePublisher[T] = db.stream(a)

  def streamTransaction[T, E <: Effect.Write](a: DBIOAction[_, Streaming[T], E]): DatabasePublisher[T] =
    db.stream(a.transactionally)

  @inline private def wrapperLogging[T](f: Future[T]): Future[T] =
    f.andThen { case Failure(e) => logger.warn(s"Slick run error [${e.toString}].") }(db.ioExecutionContext)

  override def toString = s"SqlSystem($dataSource, $jdbcTemplate, $db)"
}

object SqlSystem extends FusionExtensionId[SqlSystem] {
  override def createExtension(system: ExtendedActorSystem): SqlSystem = new SqlSystem(system)
}
