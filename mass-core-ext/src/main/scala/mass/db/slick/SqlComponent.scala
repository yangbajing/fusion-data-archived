package mass.db.slick

import akka.Done
import akka.actor.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import com.zaxxer.hikari.HikariDataSource
import fusion.core.extension.FusionCore
import fusion.jdbc.{ FusionJdbc, JdbcTemplate }
import javax.inject.{ Inject, Singleton }
import slick.basic.DatabasePublisher

import scala.concurrent.Future
import scala.util.Failure

/**
 * Mass系统SQL数据访问管理器
 */
@Singleton
class SqlComponent @Inject() (val profile: PgProfile, classicSystem: ActorSystem) extends StrictLogging {
  import profile.api._
  val dataSource: HikariDataSource = FusionJdbc(classicSystem).component
  val db = databaseForDataSource(dataSource)
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
