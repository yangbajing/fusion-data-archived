package mass.connector.sql

import java.util.Properties
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep}
import com.zaxxer.hikari.HikariDataSource
import helloscala.common.types.AsString
import mass.core.jdbc.JdbcUtils
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpec}

import scala.collection.immutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Ys2LocalTest extends WordSpec with MustMatchers with BeforeAndAfterAll {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()
  val localDS = createPGDataSource()
  val ysDS = createYsPGDataSource()

  "migrate" in {
    val columns = immutable.IndexedSeq("id",
                                       "ban_id",
                                       "banletter",
                                       "banlettpiny",
                                       "bantype",
                                       "banexp",
                                       "banform",
                                       "banto",
                                       "recper",
                                       "recdate")

    // 从源读取
    val source = JdbcSource(s"SELECT ${columns.mkString(", ")} FROM haishu_ys.reg_name_baninfo;", Nil, 1500)(ysDS)

    // 将第一个元素值改成小写
    def transferFlow(idxBanId: Int) = Flow[JdbcResultSet].map { jrs =>
      if (jrs.values.length > 1) {
        jrs.values(idxBanId) match {
          case null => jrs
          case value =>
            jrs.copy(values = jrs.values.updated(1, AsString.unapply(value).orNull))
        }
      } else
        jrs
    }

    val sink = JdbcSink[JdbcResultSet](
      conn =>
        conn.prepareStatement(
          """INSERT INTO reg_name_baninfo (id, ban_id, banletter, banlettpiny, bantype, banexp, banform, banto, recper, recdate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""),
      (jrs, stmt) => columns.indices.foreach(i => stmt.setObject(i + 1, jrs.values(i))),
      1000
    )(localDS)

    val graph = source
      .via(JdbcFlow.flowJdbcResultSet)
      .via(transferFlow(1))
      .toMat(sink)(Keep.right)

    val begin = System.nanoTime()
    val f = graph.run()
    val result = Await.result(f, Duration.Inf)
    val end = System.nanoTime()

    val costTime = java.time.Duration.ofNanos(end - begin)
    println(s"从 155 导 reg_name_baninfo 表的 ${result.count} 条数据到本地共花费时间：$costTime")
  }

  override protected def beforeAll(): Unit =
    TimeUnit.SECONDS.sleep(3) // JVM预热

  override protected def afterAll(): Unit = {
    localDS.close()
    ysDS.close()
    system.terminate()
  }

  private def createPGDataSource(): HikariDataSource = {
    val props = new Properties()
    props.setProperty("poolName", "local")
    props.setProperty("maximumPoolSize", "4")
    props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource")
    props.setProperty("dataSource.serverName", "localhost")
    props.setProperty("dataSource.databaseName", "yangbajing")
    props.setProperty("dataSource.user", "yangbajing")
    props.setProperty("dataSource.password", "yangbajing")
    JdbcUtils.createHikariDataSource(props)
  }

  private def createYsPGDataSource(): HikariDataSource =
    JdbcUtils.createHikariDataSource(
      ("poolName", "ys"),
      ("maximumPoolSize", "4"),
      ("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource"),
      ("dataSource.serverName", "192.168.32.155"),
      ("dataSource.portNumber", "10032"),
      ("dataSource.databaseName", "postgres"),
      ("dataSource.user", "postgres"),
      ("dataSource.password", "hl.Data2018")
    )

}
