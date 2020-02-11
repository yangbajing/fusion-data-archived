package mass.adhoc.s4yd

import java.time.{ Instant, OffsetDateTime }
import java.util.UUID

import fusion.jdbc.JdbcTemplate
import fusion.jdbc.util.JdbcUtils
import helloscala.common.test.HelloscalaSpec
import helloscala.common.types.{ AsBoolean, AsLong, AsString }
import helloscala.common.util.{ StringUtils, TimeUtils }
import org.scalatest.BeforeAndAfterAll

import scala.io.Source

class ArticleCategoryTest extends HelloscalaSpec with BeforeAndAfterAll {
  val s4yd = JdbcTemplate(
    JdbcUtils.createHikariDataSource(Map(
      "poolName" -> "s4yd",
      "jdbcUrl" -> "jdbc:mysql://cqph96:3306/s4yd?useSSL=false",
      "username" -> sys.props("mysql.username"),
      "password" -> sys.props("mysql.password"))))

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
      "numThreads" -> "2")))

  "ArticleCategory" should {
    "Category from s4yd to reading" in {
      s4yd.listForMap("select * from rv_article_category", Nil).foreach { data =>
        reading.update(
          """insert into td_art_book_classify(id, parent_id, name, create_by, create_time)
                         |values (?, ?, ?, 1, now())
                         |on conflict(id)
                         |   do update set parent_id = EXCLUDED.parent_id, name = EXCLUDED.name;""".stripMargin,
          List(data("id"), data("parent_id"), data("name")))
      }
    }

    "Book from s4yd to reading" in {
      val id2ParentIds = reading
        .listForMap("select * from td_art_book_classify", Nil)
        .map(data => data("id").asInstanceOf[Long] -> data("parent_id").asInstanceOf[Long])
        .toMap

      val sql = "select * from rv_article where id in " + bookIds.map(_ => "?").mkString("(", ",", ")")
      val books = s4yd.listForMap(sql, bookIds)
      val insertSql =
        """insert into td_art_book(author_id, id, name, key_words, intro, progress, type_id, need_vip, sex, show_id, check_status, create_time, create_by)
                        |values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        |on conflict (
                        |   id)
                        |   do update set
                        |     author_id = EXCLUDED.author_id,
                        |     name = EXCLUDED.name,
                        |     key_words = EXCLUDED.key_words,
                        |     intro = EXCLUDED.intro,
                        |     progress = EXCLUDED.progress,
                        |     type_id = EXCLUDED.type_id,
                        |     need_vip = EXCLUDED.need_vip,
                        |     sex = EXCLUDED.sex,
                        |     check_status = EXCLUDED.check_status,
                        |     create_time = EXCLUDED.create_time,
                        |     create_by = EXCLUDED.create_by""".stripMargin
      books.foreach { data =>
        val originalCategory = AsLong.unapply(data("category")).get
        val sex = id2ParentIds(originalCategory)
        val args = List(
          AsLong.unapply(data("author")).get, // int(10)
          AsLong.unapply(data("id")).get, // int(10)
          AsString.unapply(data("name")).get, // varchar(255)
          AsString
            .unapply(data("keywords"))
            .map(
              _.replaceAll("，", ",")
                .replaceAll(" ", ",")
                .replaceAll(";", ",")
                .split(',')
                .filterNot(StringUtils.isBlank)
                .mkString(","))
            .getOrElse(""), // varchar(255)
          AsString.unapply(data("intro")).getOrElse(""), // text
          AsBoolean.unapply(data("progress")).map(b => if (b) "1" else "0").getOrElse("0"), // tinyint(1)
          AsLong.unapply(data("subcategory")).get,
          AsBoolean.unapply(data("isvip")).map(b => if (b) "1" else "0").getOrElse("0"),
          if (sex == 2L) 0L else 1L,
          UUID.randomUUID().toString,
          1,
          AsLong
            .unapply(data("create_time"))
            .map(s => Instant.ofEpochSecond(s).atOffset(TimeUtils.ZONE_CHINA_OFFSET))
            .getOrElse(OffsetDateTime.now()),
          1L)
        args.foreach(_.getClass)
        val ret = reading.update(insertSql, args)
        println(ret)
      }
    }

    "Book's Cover from s4yd to reading" in {
      val articles =
        s4yd.listForObject("select id, name, cover from rv_article", Nil, JdbcUtils.resultSetToBean[RvArticle])
//      articles.foreach(a => println(Jackson.stringify(a)))
//      println(s"article size is ${articles.size}")
      for (a <- articles if (a.cover ne null) && StringUtils.isNoneBlank(a.cover)) {
        val baseFile = RvBaseFile(
          a.cover.split('/').lastOption.getOrElse(""),
          a.cover.split('.').lastOption.getOrElse(""),
          1L,
          OffsetDateTime.now(),
          "1",
          "/opt/haishu/app/reading/upload-file" + a.cover)
        println(baseFile)
        val coverId = reading
          .findForObject(
            """insert into td_base_file(origin_name, suffixes, create_by, create_time, status, path)
                                |values (?, ?, ?, ?, ?, ?) returning id;""".stripMargin,
            baseFile.productIterator.toList,
            _.getLong(1))
          .get
        val ret = reading.update("update td_art_book set cover_id = ? where id = ?;", List(coverId, a.id))
        println(s"update book cover_id return is $ret")
      }
    }

    "Part from s4yd to reading" in {
      val insertSql = """insert into td_art_part(id, name, book_id, context, sort, create_time, create_by)
                        |values (?, ?, ?, ?, ?, ?, ?)
                        |on conflict(id)
                        |   do update set
                        |     name = EXCLUDED.name,
                        |     book_id = EXCLUDED.book_id,
                        |     context = EXCLUDED.context,
                        |     sort = EXCLUDED.sort,
                        |     create_time = EXCLUDED.create_time,
                        |     create_by = EXCLUDED.create_by;""".stripMargin
      val volumes = s4yd.listForObject(
        "select id, sortname, name, article_id, serial_no, description, create_time from rv_article_volume;",
        Nil,
        JdbcUtils.resultSetToBean[ArticleVolume])
//      volumes.foreach(println)
      volumes.foreach { v =>
        val p = ArtPart(v.id, v.name, v.articleId, v.description, v.serialNo, v.createTime)
        val ret = reading.update(insertSql, p.productIterator.toList)
        println(ret)
      }
    }

    "Article from s4yd to reading" in {
      val selectSql = """select rv_article.author, chapter.*
                        |from (select a.*, c.content
                        |      from rv_article_chapter a,
                        |           rv_article_content c
                        |      where a.id = c.id and a.article_id = ?) chapter,
                        |     rv_article
                        |where rv_article.id = chapter.article_id;""".stripMargin
      val insertSql =
        """insert into td_art_article(id, create_by, create_time, title, context, book_id, part_id, words_size, check_status,
                        |                           show_id, sort, need_vip, publish_time, draft_status)
                        |values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        |on conflict (
                        |   id)
                        |   do update set
                        |     create_by = EXCLUDED.create_by,
                        |     create_time = EXCLUDED.create_time,
                        |     title = EXCLUDED.title,
                        |     context = EXCLUDED.context,
                        |     book_id = EXCLUDED.book_id,
                        |     part_id = EXCLUDED.part_id,
                        |     words_size = EXCLUDED.words_size,
                        |     check_status = EXCLUDED.check_status,
                        |     sort = EXCLUDED.sort,
                        |     need_vip = EXCLUDED.need_vip,
                        |     publish_time = EXCLUDED.publish_time,
                        |     draft_status = EXCLUDED.draft_status;""".stripMargin
      val updateNewest = """update td_art_book
                           |set newly_title       = ?,
                           |    newly_update_time = ?
                           |where id = ?
                           |""".stripMargin
      for (bookId <- bookIds) {
        val chapters = s4yd.listForObject(selectSql, List(bookId), JdbcUtils.resultSetToBean[RvArticle])
//        chapters.foreach(c => println(Jackson.stringify(c)))
        println(s"chapters size: ${chapters.size}")

        val articles = chapters.map { c =>
          ArtArticle(
            c.id,
            c.author,
            c.createTime,
            c.name,
            c.content,
            c.articleId,
            c.volumeId,
            c.size,
            c.audited.toString,
            UUID.randomUUID().toString,
            c.order,
            c.isvip.toString,
            c.publishTime)
        }

        if (articles.nonEmpty) {
          val a = articles.maxBy(_.sort)
          reading.update(updateNewest, List(a.title, a.createTime, a.bookId))
        }
        val ret = reading.updateBatch(insertSql, articles.map(_.productIterator.toList))
        println(s"insert size: ${ret.sum}")
      }
    }

    "User from s4yd to reading" in {
      val BATCH_COUNT = 500
      val userCount = s4yd.size("select count(*) from rv_user", Nil)
      val selectSql = """select u.*,
                        |       p.nickname,
                        |       p.realname,
                        |       p.penname,
                        |       p.gender,
                        |       p.avatar,
                        |       p.idcardtype,
                        |       p.idcard,
                        |       p.qq,
                        |       p.birthday,
                        |       p.constellation,
                        |       p.zodiac,
                        |       p.province,
                        |       p.city,
                        |       p.address,
                        |       p.mobile as profile_mobile
                        |from rv_user u,
                        |     rv_user_profile p
                        |where u.id = p.id
                        |order by id asc
                        |limit ? offset ?
                        |;""".stripMargin
      val saveUserSql =
        """insert into td_usr_user(create_time, create_by, status, account, vip, nick_name, email, phone, cover_id, id)
                        |values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        |on conflict(
                        |   id)
                        |   do update set
                        |     create_time = EXCLUDED.create_time,
                        |     create_by = EXCLUDED.create_by,
                        |     status = EXCLUDED.status,
                        |     account = EXCLUDED.account,
                        |     vip = EXCLUDED.vip,
                        |     nick_name = EXCLUDED.nick_name,
                        |     email = EXCLUDED.email,
                        |     phone = EXCLUDED.phone,
                        |     cover_id = EXCLUDED.cover_id
                        |;""".stripMargin
      val saveUserAuthorizationSql =
        """insert into td_usr_authorization(user_id, salt, password, id_card, card_type, address, true_name, province)
                                       |values (?, ?, ?, ?, ?, ?, ?, ?)
                                       |on conflict(
                                       |   user_id)
                                       |   do update set
                                       |     salt = EXCLUDED.salt,
                                       |     password = EXCLUDED.password,
                                       |     id_card = EXCLUDED.id_card,
                                       |     card_type = EXCLUDED.card_type,
                                       |     address = EXCLUDED.address,
                                       |     true_name = EXCLUDED.true_name,
                                       |     province = EXCLUDED.province
                                       |;""".stripMargin
      val saveUserAuthorSql = """insert into td_usr_author(user_id,
                                |                          id,
                                |                          name,
                                |                          pen_name,
                                |                          create_by,
                                |                          create_time,
                                |                          intro,
                                |                          works_num,
                                |                          words_size,
                                |                          work_day_num,
                                |                          check_status)
                                |values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                                |on conflict(user_id)
                                |   do update set
                                |     id = EXCLUDED.id,
                                |     name = EXCLUDED.name,
                                |     pen_name = EXCLUDED.pen_name,
                                |     create_by = EXCLUDED.create_by,
                                |     create_time = EXCLUDED.create_time,
                                |     intro = EXCLUDED.intro,
                                |     works_num = EXCLUDED.works_num,
                                |     words_size = EXCLUDED.words_size,
                                |     work_day_num = EXCLUDED.work_day_num,
                                |     check_status = EXCLUDED.check_status
                                |;""".stripMargin

      var offset = 0
      while (offset < userCount) {
        saveUser(selectSql, saveUserSql, saveUserAuthorizationSql, saveUserAuthorSql, offset, BATCH_COUNT)
        offset += BATCH_COUNT
      }
      if (offset < userCount) {
        saveUser(selectSql, saveUserSql, saveUserAuthorizationSql, saveUserAuthorSql, offset, BATCH_COUNT)
      }

//      users.foreach(u => println(Jackson.stringify(u)))
//      println(s"user size is $size")
    }

    "show bookIds" in {
      println(bookIds.mkString("(", ",", ")"))
    }
  }

  private def saveUser(
      selectSql: String,
      saveUserSql: String,
      saveUserAuthorizationSql: String,
      saveUserAuthorSql: String,
      offset: Int,
      limit: Int): Unit = {
    val users = s4yd.listForObject(selectSql, List(limit, offset), JdbcUtils.resultSetToBean[RvUser])
    val (payloads, authorPayloads) = users.map { user =>
      val u = TdUser(
        user.regtime,
        1L,
        1,
        user.username,
        user.vip.toString,
        user.username,
        user.email,
        if (user.mobile.forall(_.isDigit)) user.mobile else null,
        0,
        user.id)

      val p = TdUserAuthor(user.id, user.id, user.realname, user.penname, 1L, user.regtime, "", 0, 0L, 0, "1")

      (u, p)
    }.unzip
    val rets = reading.updateBatch(saveUserSql, payloads.map(_.productIterator.toList))

    val saveUserAuthRets = reading.updateBatch(
      saveUserAuthorizationSql,
      users.map(
        user =>
          List(
            user.id,
            "DGXGZ7C2R0SMKFAE1A1HLV0R2A8HM2AP",
            "7cdd43adc02e695ea3d0e1bb636a9daafacdea510aa475ed010b9c9bed6924f6")))

    reading.updateBatch(saveUserAuthorSql, authorPayloads.map(_.productIterator.toList))

    println("rets: " + rets.toList)
    println("saveUserAuthRets: " + saveUserAuthRets.toList)
  }

  override protected def afterAll(): Unit = {
    JdbcUtils.closeDataSource(s4yd.dataSource)
    JdbcUtils.closeDataSource(reading.dataSource)
    super.afterAll()
  }

  lazy val bookIds = Source.fromString(BOOK_IDS).getLines().map(_.trim.split('/').last).toList

  val BOOK_IDS = """http://www.s4yd.com/book/583
                   |http://www.s4yd.com/book/469
                   |http://www.s4yd.com/book/708
                   |http://www.s4yd.com/book/202
                   |http://www.s4yd.com/book/175
                   |http://www.s4yd.com/book/579
                   |http://www.s4yd.com/book/386
                   |http://www.s4yd.com/book/296
                   |http://www.s4yd.com/book/2325
                   |http://www.s4yd.com/book/1511
                   |http://www.s4yd.com/book/1431
                   |http://www.s4yd.com/book/1121
                   |http://www.s4yd.com/book/1962
                   |http://www.s4yd.com/book/394
                   |http://www.s4yd.com/book/534
                   |http://www.s4yd.com/book/1451
                   |http://www.s4yd.com/book/2360
                   |http://www.s4yd.com/book/998
                   |http://www.s4yd.com/book/2351
                   |http://www.s4yd.com/book/2357
                   |http://www.s4yd.com/book/1454
                   |http://www.s4yd.com/book/1948
                   |http://www.s4yd.com/book/851
                   |http://www.s4yd.com/book/323
                   |http://www.s4yd.com/book/2456
                   |http://www.s4yd.com/book/2446
                   |http://www.s4yd.com/book/519
                   |http://www.s4yd.com/book/1083
                   |http://www.s4yd.com/book/855
                   |http://www.s4yd.com/book/2329
                   |http://www.s4yd.com/book/2460
                   |http://www.s4yd.com/book/2470
                   |http://www.s4yd.com/book/2492
                   |http://www.s4yd.com/book/2502
                   |http://www.s4yd.com/book/1887
                   |http://www.s4yd.com/book/1919
                   |http://www.s4yd.com/book/2321
                   |http://www.s4yd.com/book/2350
                   |http://www.s4yd.com/book/1483
                   |http://www.s4yd.com/book/2378
                   |http://www.s4yd.com/book/2428
                   |http://www.s4yd.com/book/2441
                   |http://www.s4yd.com/book/2535
                   |http://www.s4yd.com/book/2530
                   |http://www.s4yd.com/book/2448
                   |http://www.s4yd.com/book/2549
                   |http://www.s4yd.com/book/176
                   |http://www.s4yd.com/book/216
                   |http://www.s4yd.com/book/349
                   |http://www.s4yd.com/book/143
                   |http://www.s4yd.com/book/348
                   |http://www.s4yd.com/book/520
                   |http://www.s4yd.com/book/239
                   |http://www.s4yd.com/book/380
                   |http://www.s4yd.com/book/314
                   |http://www.s4yd.com/book/186
                   |http://www.s4yd.com/book/460
                   |http://www.s4yd.com/book/499
                   |http://www.s4yd.com/book/504
                   |http://www.s4yd.com/book/549
                   |http://www.s4yd.com/book/618
                   |http://www.s4yd.com/book/876
                   |http://www.s4yd.com/book/500
                   |http://www.s4yd.com/book/1169
                   |http://www.s4yd.com/book/126
                   |http://www.s4yd.com/book/1520
                   |http://www.s4yd.com/book/2538""".stripMargin
}
