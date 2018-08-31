package mass.slick

import java.sql.{Date, Time, Timestamp}
import java.util.UUID
import java.util.concurrent.TimeUnit

import com.github.tminglei.slickpg.utils.SimpleArrayUtils
import helloscala.common.data.NameValue
import helloscala.common.jackson.Jackson
import helloscala.common.types.ObjectId
import massmsg.CommonStatus
import slick.jdbc.{GetResult, JdbcType, SetParameter}

import scala.concurrent.duration.FiniteDuration
import scala.language.{higherKinds, implicitConversions}

trait MassSlickSupport { driver: SlickProfile =>

  import driver.api._

  trait MassPlainImplicits {
    import com.github.tminglei.slickpg.utils.PlainSQLUtils._

    implicit val getCommonStatus: GetResult[CommonStatus] = mkGetResult(pr => CommonStatus.fromValue(pr.nextInt()))
    implicit val getCommonStatusOption: GetResult[Option[CommonStatus]] =
      mkGetResult(_.nextIntOption().map(CommonStatus.fromValue))
    implicit val setCommonStatus: SetParameter[CommonStatus] = (v, rp) => rp.setInt(v.value)
    implicit val setCommonStatusOption: SetParameter[Option[CommonStatus]] = (v, rp) => rp.setIntOption(v.map(_.value))

    implicit val getFiniteDuration: GetResult[FiniteDuration] =
      mkGetResult(pr => FiniteDuration(pr.nextLong(), TimeUnit.MILLISECONDS))
    implicit val getFiniteDurationOption: GetResult[Option[FiniteDuration]] =
      mkGetResult(pr => pr.nextLongOption().map(FiniteDuration(_, TimeUnit.MILLISECONDS)))
    implicit val setFiniteDuration: SetParameter[FiniteDuration] = (v, rp) => rp.setLong(v.toMillis)
    implicit val setFiniteDurationOption: SetParameter[Option[FiniteDuration]] = (v, rp) =>
      rp.setLongOption(v.map(_.toMillis))
  }

  trait MassSlickImplicits {
    implicit def commonStatusColumnType: JdbcType[CommonStatus] =
      MappedColumnType.base[CommonStatus, Int](_.value, id => CommonStatus.fromValue(id))

    implicit val durationColumnType: JdbcType[FiniteDuration] =
      MappedColumnType.base[FiniteDuration, Long](_.toMillis, millis => FiniteDuration(millis, TimeUnit.MILLISECONDS))

    implicit val passportColumnType: JdbcType[NameValue] =
      MappedColumnType.base[NameValue, String](Jackson.defaultObjectMapper.writeValueAsString,
                                               Jackson.defaultObjectMapper.readValue(_, classOf[NameValue]))

    implicit val objectIdTypeMapper: JdbcType[ObjectId] =
      MappedColumnType.base[ObjectId, String](_.toString(), ObjectId.apply)

    implicit val objectIdListTypeMapper: AdvancedArrayJdbcType[ObjectId] =
      new AdvancedArrayJdbcType[ObjectId](
        "text",
        s => SimpleArrayUtils.fromString[ObjectId](ObjectId.apply)(s).orNull,
        v => SimpleArrayUtils.mkString[ObjectId](_.toString())(v)
      )

    type FilterCriteriaType = Option[Rep[Option[Boolean]]]

    def dynamicFilter(list: Seq[FilterCriteriaType]): Rep[Option[Boolean]] =
      list
        .collect({ case Some(criteria) => criteria })
        .reduceLeftOption(_ && _)
        .getOrElse(Some(true): Rep[Option[Boolean]])

    def dynamicFilter(item: Option[Rep[Boolean]], list: Option[Rep[Boolean]]*): Rep[Boolean] =
      (item +: list).collect({ case Some(criteria) => criteria }).reduceLeftOption(_ && _).getOrElse(true: Rep[Boolean])

    def dynamicFilterOr(list: Seq[FilterCriteriaType]): Rep[Option[Boolean]] =
      list
        .collect({ case Some(criteria) => criteria })
        .reduceLeftOption(_ || _)
        .getOrElse(Some(true): Rep[Option[Boolean]])

  }

  trait MassArrayImplicits extends SimpleArrayCodeGenSupport {

    /** for type/name, @see [[org.postgresql.core.Oid]] and [[org.postgresql.jdbc.TypeInfoCache]]*/
    implicit val simpleUUIDListTypeMapper: JdbcType[Seq[UUID]] = new SimpleArrayJdbcType[UUID]("uuid")
    implicit val simpleStrListTypeMapper: JdbcType[Seq[String]] = new SimpleArrayJdbcType[String]("text")
    implicit val simpleLongListTypeMapper: JdbcType[Seq[Long]] = new SimpleArrayJdbcType[Long]("int8")
    implicit val simpleIntListTypeMapper: JdbcType[Seq[Int]] = new SimpleArrayJdbcType[Int]("int4")
    implicit val simpleShortListTypeMapper: JdbcType[Seq[Short]] = new SimpleArrayJdbcType[Short]("int2")
    implicit val simpleFloatListTypeMapper: JdbcType[Seq[Float]] = new SimpleArrayJdbcType[Float]("float4")
    implicit val simpleDoubleListTypeMapper: JdbcType[Seq[Double]] = new SimpleArrayJdbcType[Double]("float8")
    implicit val simpleBoolListTypeMapper: JdbcType[Seq[Boolean]] = new SimpleArrayJdbcType[Boolean]("bool")
    implicit val simpleDateListTypeMapper: JdbcType[Seq[Date]] = new SimpleArrayJdbcType[Date]("date")
    implicit val simpleTimeListTypeMapper: JdbcType[Seq[Time]] = new SimpleArrayJdbcType[Time]("time")
    implicit val simpleTsListTypeMapper: JdbcType[Seq[Timestamp]] = new SimpleArrayJdbcType[Timestamp]("timestamp")

    ///
    implicit def simpleArrayColumnExtensionMethods[B1, SEQ[B1] <: Seq[B1]](
        c: Rep[SEQ[B1]])(implicit tm: JdbcType[B1], tm1: JdbcType[SEQ[B1]]) =
      new ArrayColumnExtensionMethods[B1, SEQ, SEQ[B1]](c)
    implicit def simpleArrayOptionColumnExtensionMethods[B1, SEQ[B1] <: Seq[B1]](
        c: Rep[Option[SEQ[B1]]])(implicit tm: JdbcType[B1], tm1: JdbcType[SEQ[B1]]) =
      new ArrayColumnExtensionMethods[B1, SEQ, Option[SEQ[B1]]](c)
  }

}
