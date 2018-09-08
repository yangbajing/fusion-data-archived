package helloscala.common.util

import java.time.{LocalDateTime, OffsetDateTime, ZoneOffset, ZonedDateTime}

import helloscala.common.test.HelloscalaSpec

class TimeUtilsTest extends HelloscalaSpec {

  "TimeUtilsTest" must {
    "0 toOffsetDateTime" in {
      val odt = TimeUtils.toOffsetDateTime(0L)
      println(odt)
      println(odt.toInstant.toEpochMilli)

      val odt2 = TimeUtils.toOffsetDateTime(OffsetDateTime.now().toString)
      println(odt2)

      val d3 = OffsetDateTime.now().format(TimeUtils.formatterDateTime)
      println(d3)
      val odt3 = TimeUtils.toOffsetDateTime(d3)
      println(odt3)
    }

    "toLocalDate" in {
      val strLdt = "2017-01-23  22:32:11"
      TimeUtils.toLocalDateTime(strLdt) mustBe LocalDateTime.of(2017, 1, 23, 22, 32, 11)
    }

    "toZonedDateTime" in {
      TimeUtils.toZonedDateTime("2018-1-2 11:11:11+08") mustBe ZonedDateTime.of(2018,
                                                                                1,
                                                                                2,
                                                                                11,
                                                                                11,
                                                                                11,
                                                                                0,
                                                                                ZoneOffset.ofHours(8))
      TimeUtils.toZonedDateTime("2018-1-2 11:11:11+08:00") mustBe ZonedDateTime
        .of(2018, 1, 2, 11, 11, 11, 0, ZoneOffset.ofHours(8))
      TimeUtils.toZonedDateTime("2018-1-2 11:11:11-08") mustBe ZonedDateTime.of(2018,
                                                                                1,
                                                                                2,
                                                                                11,
                                                                                11,
                                                                                11,
                                                                                0,
                                                                                ZoneOffset.ofHours(-8))
      TimeUtils.toZonedDateTime("2018-1-2T11:11:11-08:00") mustBe ZonedDateTime
        .of(2018, 1, 2, 11, 11, 11, 0, ZoneOffset.ofHours(-8))
      TimeUtils.toZonedDateTime("2018-11-2T11:11:11-08:30") mustBe ZonedDateTime
        .of(2018, 11, 2, 11, 11, 11, 0, ZoneOffset.of("-08:30"))
      TimeUtils.toZonedDateTime("2018-1-2T11:11:11") mustBe ZonedDateTime.of(2018,
                                                                             1,
                                                                             2,
                                                                             11,
                                                                             11,
                                                                             11,
                                                                             0,
                                                                             ZoneOffset.ofHours(8))
      TimeUtils.toZonedDateTime("2018-1-22 11:11:11") mustBe ZonedDateTime.of(2018,
                                                                              1,
                                                                              22,
                                                                              11,
                                                                              11,
                                                                              11,
                                                                              0,
                                                                              ZoneOffset.ofHours(8))
      TimeUtils.toZonedDateTime("2018-01-22 11:11:11") mustBe ZonedDateTime.of(2018,
                                                                               1,
                                                                               22,
                                                                               11,
                                                                               11,
                                                                               11,
                                                                               0,
                                                                               ZoneOffset.ofHours(8))
      TimeUtils.toZonedDateTime("2018-01-22 11:11:11.321") mustBe ZonedDateTime
        .of(2018, 1, 22, 11, 11, 11, 321 * 1000 * 1000, ZoneOffset.ofHours(8))
      TimeUtils.toZonedDateTime("2018-01-22 11:11:11.321+8") mustBe ZonedDateTime
        .of(2018, 1, 22, 11, 11, 11, 321 * 1000 * 1000, ZoneOffset.ofHours(8))
    }
  }

}
