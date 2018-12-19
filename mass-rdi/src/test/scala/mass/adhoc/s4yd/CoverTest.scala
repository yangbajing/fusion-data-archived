package mass.adhoc.s4yd

import java.net.InetAddress
import java.nio.file.attribute.PosixFilePermission
import java.time.OffsetDateTime

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.ftp.FtpCredentials.NonAnonFtpCredentials
import akka.stream.alpakka.ftp.SftpSettings
import akka.stream.alpakka.ftp.scaladsl.Sftp
import helloscala.common.test.HelloscalaSpec
import mass.core.jdbc.{JdbcTemplate, JdbcUtils}
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Cover {
  var id: Long = _
  var cover: String = _
}

class BaseFile {
  var id: Long = _
  var path: String = _
}

class CoverTest extends HelloscalaSpec with BeforeAndAfterAll {

  implicit val system = ActorSystem()
  implicit val mat = ActorMaterializer()

  val s4yd = JdbcTemplate(
    JdbcUtils.createHikariDataSource(
      Map(
        "poolName" -> "s4yd",
        "jdbcUrl" -> "jdbc:mysql://cqph96:3306/s4yd?useSSL=false",
        "username" -> sys.props("mysql.username"),
        "password" -> sys.props("mysql.password")
      )))

  val reading = JdbcTemplate(
    JdbcUtils.createHikariDataSource(Map(
      "poolName" -> "reading",
      "dataSourceClassName" -> "org.postgresql.ds.PGSimpleDataSource",
      "dataSource.serverName" -> "cqph96",
      "dataSource.portNumber" -> "5432",
      "dataSource.databaseName" -> "reading_system",
      "dataSource.user" -> sys.props("pg.user"),
      "dataSource.password" -> sys.props("pg.password"),
      "maximumPoolSize" -> "2",
      "allowPrintLog" -> "true",
      "numThreads" -> "2",
    )))

  "ArticleCategory" should {
    "Book's Cover from s4yd to reading" in {
      val updateBookSql = "update td_art_book set cover_id = ? where id = ? and cover_id is null;"
      val updateConfigRecommend = "update td_config_recommend set cover_id = ? where book_id = ? and cover_id is null;"

      val books = s4yd.listForObject("select id, cover from rv_article where cover is not null and cover != '';",
                                     Nil,
                                     JdbcUtils.resultSetToBean[Cover])

      def selectCoverIdPath(path: String) =
        reading.findForObject("select id, path from td_base_file where path = ?;",
                              List(path),
                              JdbcUtils.resultSetToBean[BaseFile])

      books.foreach { book =>
        val mayBeBaseFile = selectCoverIdPath("/opt/haishu/app/reading/upload-file" + book.cover)
        mayBeBaseFile.foreach { baseFile =>
          reading.update(updateBookSql, List(baseFile.id, book.id))
          reading.update(updateConfigRecommend, List(baseFile.id, book.id))

          println(s"cover_id: ${baseFile.id}, book_id: ${book.id}")
        }
      }
    }

    "file to base_file" in {
      val updateBaseFileSql =
        """insert into td_base_file(origin_name, suffixes, create_by, create_time, status, path)
          |values (?, ?, ?, ?, ?, ?)
          |on conflict(path)
          |   do update set
          |     origin_name = EXCLUDED.origin_name,
          |     suffixes = EXCLUDED.suffixes,
          |     status = EXCLUDED.status;
          |""".stripMargin
      val ftpSettings = SftpSettings(
        InetAddress.getByName("cqph96"),
        22,
        NonAnonFtpCredentials("haishu", "Hlw..2018"),
        strictHostKeyChecking = false
      )

      val source = Sftp.ls("/opt/haishu/app/reading/upload-file/covers", ftpSettings)
      val now = OffsetDateTime.now()
      val f = source
        .filter(file => file.isFile && file.permissions.contains(PosixFilePermission.OTHERS_READ))
        //        .runFold(0)((n, _) => n + 1)
        //        .runForeach(println)
        .grouped(500)
        .runForeach { files =>
          val argsList =
            files.map(file => List(file.name, file.name.split('.').lastOption.orNull, 1L, now, "1", file.path.toString))
          val ret = reading.updateBatch(updateBaseFileSql, argsList)
          println(java.util.Arrays.toString(ret))
        }
      val result = Await.result(f, Duration.Inf)
      println(s"result: $result")
    }
  }

  override protected def afterAll(): Unit = {
    JdbcUtils.closeDataSource(s4yd.dataSource)
    JdbcUtils.closeDataSource(reading.dataSource)
    system.terminate()
    super.afterAll()
  }
}
