package mass.connector.sql

import java.sql.ResultSet
import java.time.Instant
import java.util.Properties

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import helloscala.common.test.HelloscalaSpec
import mass.core.jdbc.JdbcUtils
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.Future

case class Book(isbn: String, title: String, createdAt: Instant)

class JdbcTemplateTest extends HelloscalaSpec with BeforeAndAfterAll {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  val pgDataSource = createPGDataSource()
  val mysqlDataSource = createMySQLDataSource()

  "migrate" should {
    "mysql2pg" in {
      val mysqlSource = JdbcSource("SELECT isbn, title, created_at FROM t_book",
                                   Nil,
                                   100)(pgDataSource)

      val flow: Flow[ResultSet, Book, NotUsed] = Flow[ResultSet].map { rs =>
        Book(rs.getString("isbn"),
             rs.getString("title"),
             rs.getTimestamp("created_at").toInstant)
      }

      val sink: Sink[Book, Future[JdbcSinkResult]] = JdbcSink[Book](
        conn =>
          conn.prepareStatement(
            "INSERT INTO t_book(isbn, title, created_at) VALUES(?, ?, ?)"),
        (book, stmt) =>
          JdbcUtils.setStatementParameters(stmt, book.productIterator.toList),
        100
      )(mysqlDataSource)

      val results = mysqlSource.via(flow).runWith(sink).futureValue
      println(s"count: ${results.count}")
      results.results must not be empty
    }
  }

  "AkkaStream" should {
    "sink" in {
      type V = (Int, String, Instant)
      val items = List[V]((16, "16", Instant.now()), (17, "17", Instant.now()))
      val sink = JdbcSink[V](
        conn =>
          conn.prepareStatement(
            "INSERT INTO public.t_role(id, name, created_at) VALUES(?, ?, ?)"),
        (value, pstmt) =>
          JdbcUtils.setStatementParameters(pstmt, value.productIterator.toList),
        100
      )(pgDataSource)
      val results = Source(items).runWith(sink).futureValue
      println(s"count: ${results.count}")
      results.results must not be empty
    }

    "source" in {
      val source = JdbcSource("SELECT id, name, created_at FROM public.t_role",
                              Nil,
                              100)(pgDataSource)
      val future = source.zipWithIndex
        .map {
          case (rs, idx) =>
            (idx + 1,
             rs.getInt("id"),
             rs.getString("name"),
             rs.getTimestamp("created_at"))
        }
        .runWith(Sink.seq)
      val results = future.futureValue
      results.foreach(println)
      results must not be empty
    }
  }

  override protected def afterAll(): Unit = {
    pgDataSource.close()
    mysqlDataSource.close()
    system.terminate()
  }

  private def createPGDataSource(): HikariDataSource = {
    val props = new Properties()
    props.setProperty("poolName", "mass-pg-1")
    props.setProperty("maximumPoolSize", "4")
    props.setProperty("dataSourceClassName",
                      "org.postgresql.ds.PGSimpleDataSource")
    props.setProperty("dataSource.serverName", "localhost")
    props.setProperty("dataSource.databaseName", "yangbajing")
    props.setProperty("dataSource.user", "yangbajing")
    props.setProperty("dataSource.password", "yangbajing")

    val config = new HikariConfig(props)
    new HikariDataSource(config)
  }

  private def createMySQLDataSource(): HikariDataSource = {
    val props = new Properties()
    props.setProperty("poolName", "mass-mysql-1")
    props.setProperty("jdbcUrl", "jdbc:mysql://localhost:3306/yangbajing")
    props.setProperty("username", "yangbajing")
    props.setProperty("password", "yangbajing")
    props.setProperty("dataSource.prepStmtCacheSqlLimit", "2048")
    props.setProperty("dataSource.prepStmtCacheSize", "250")
    props.setProperty("dataSource.cachePrepStmts", "true")

    val config = new HikariConfig(props)
    new HikariDataSource(config)
  }

}
