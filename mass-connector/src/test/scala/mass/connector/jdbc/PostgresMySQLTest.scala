package mass.connector.jdbc

import java.nio.file.Paths
import java.sql.{ResultSet, Timestamp}

import akka.stream.IOResult
import akka.stream.alpakka.csv.scaladsl.{CsvFormatting, CsvParsing}
import akka.stream.scaladsl.{FileIO, Sink}
import akka.util.ByteString
import com.zaxxer.hikari.HikariDataSource
import helloscala.common.test.HelloscalaSpec
import mass.core.test.AkkaSpec

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Postgres:
 * create table t_book (
 *   id          bigint primary key,
 *   isbn        varchar(255),
 *   title       varchar(255),
 *   description text,
 *   publish_at  timestamptz,
 *   created_at  timestamptz
 * );
 *
 *
 * MySQL:
 * jdbcUrl添加参数：useSSL=false&characterEncoding=utf8
 * my.cnf 在 [mysqld] 段添加：
 * character_set_server=utf8mb4
 * default-time-zone='+8:00'
 *
 * create table t_book (
 *   id          bigint primary key,
 *   isbn        varchar(255),
 *   title       varchar(255),
 *   description text,
 *   publish_at  datetime,
 *   created_at  datetime
 * );
 */
class PostgresMySQLTest extends HelloscalaSpec with AkkaSpec {

  val dataSourcePostgres: HikariDataSource = JdbcUtils.createHikariDataSource(
    "poolName" -> "postgres",
    "maximumPoolSize" -> "2",
    "dataSourceClassName" -> "org.postgresql.ds.PGSimpleDataSource",
    "dataSource.serverName" -> "localhost",
    "dataSource.portNumber" -> "5432",
    "dataSource.databaseName" -> "massdata",
    "dataSource.user" -> "massdata",
    "dataSource.password" -> "massdata")

  val dataSourceMySQL: HikariDataSource = JdbcUtils.createHikariDataSource(
    "poolName" -> "mysql",
    "maximumPoolSize" -> "2",
    "jdbcUrl" -> "jdbc:mysql://localhost:3306/massdata?useSSL=false&characterEncoding=utf8",
    "username" -> "massdata",
    "password" -> "Massdata.2018",
    "dataSource.cachePrepStmts" -> "true",
    "dataSource.prepStmtCacheSize" -> "250",
    "dataSource.prepStmtCacheSqlLimit" -> "2048")

  "Database" should {
    "Postgres foreach" in {
      val sql = "select id, isbn, title, description, publish_at, created_at from t_book where created_at >= '2018-05-31' and created_at < '2018-06-01'"
      val source = JdbcSource(sql, Nil, 1000)(dataSourcePostgres)
      val jrs = source.via(JdbcFlow.flowJdbcResultSet).runWith(Sink.seq).futureValue
      jrs.foreach(rs => println(rs.values))
    }

    "Postgres -> MySQL" in {
      val sql = "select id, isbn, title, description, publish_at, created_at from t_book where created_at >= '2018-05-31' and created_at < '2018-06-01'"
      val source = JdbcSource(sql, Nil, 1000)(dataSourcePostgres)
      val sink = JdbcSink[ResultSet](
        conn => conn.prepareStatement("insert into t_book(id, isbn, title, description, publish_at, created_at) values (?, ?, ?, ?, ?, ?);"),
        (rs, pstmt) => {
          val meta = rs.getMetaData
          (1 to meta.getColumnCount).foreach(i => JdbcUtils.setParameter(pstmt, i, rs.getObject(i)))
        },
        1000)(dataSourceMySQL)

      val f = source.runWith(sink)
      val ret = Await.result(f, 5.minutes)
      println(s"Postgres -> MySQL result: $ret")
    }

    "MySQL -> Postgres" in {
      val sql = "select id, isbn, title, description, publish_at, created_at from t_book where created_at >= '2018-05-31' and created_at < '2018-06-01'"
      val source = JdbcSource(sql, Nil, 1000)(dataSourcePostgres)
      val sink = JdbcSink[JdbcResultSet](
        conn => conn.prepareStatement("insert into t_book(id, isbn, title, description, publish_at, created_at) values (?, ?, ?, ?, ?, ?);"),
        (jrs, pstmt) => JdbcUtils.setStatementParameters(pstmt, jrs.values.updated(0, 100L)),
        1000)(dataSourcePostgres)
      val f = source.via(JdbcFlow.flowJdbcResultSet).runWith(sink)
      val ret = Await.result(f, 1.minutes)
      println(s"MySQL -> Postgres result: $ret")
    }
  }

  "Text" should {
    "Postgres -> Text" in {
      val sql = "select id, isbn, title, description, publish_at, created_at from t_book where created_at >= '2018-05-31' and created_at < '2018-06-01'"
      val source = JdbcSource(sql, Nil, 1000)(dataSourcePostgres)
      val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get("/tmp/t_book.txt"))
      val f = source
        .via(JdbcFlow.flowToSeq)
        .map(items => items.map(_.toString))
        .via(CsvFormatting.format())
        //        .merge(Source.fromIterator(() => Iterator.continually(ByteString('\n'.toByte))), true)
        .runWith(sink)
      //        .runForeach(bytes => println(bytes.utf8String))
      val ret = Await.result(f, 1.minute)
      println(s"Postgres -> Text result: $ret")
    }

    "Text -> MySQL" in {
      val sink = JdbcSink[List[String]](
        conn => conn.prepareStatement("insert into t_book(id, isbn, title, description, publish_at, created_at) values (?, ?, ?, ?, ?, ?);"),
        (values, pstmt) => {
          val id :: isbn :: title :: description :: publishAt :: createdAt :: Nil = values
          println(values)
          JdbcUtils.setStatementParameters(pstmt, List(id.toLong, isbn, title, description, java.sql.Date.valueOf(publishAt), Timestamp.valueOf(createdAt)))
        },
        1000)(dataSourceMySQL)

      val f = FileIO.fromPath(Paths.get("/tmp/t_book.txt"))
        .via(CsvParsing.lineScanner())
        .map(list => list.map(_.utf8String))
        //        .flatMapConcat(list => Source(list))
        .runWith(sink)
      val ret = Await.result(f, 1.minute)
      println(s"ret: $ret")
    }
  }

  override protected def afterAll(): Unit = {
    dataSourceMySQL.close()
    dataSourcePostgres.close()
    super.afterAll()
  }
}
