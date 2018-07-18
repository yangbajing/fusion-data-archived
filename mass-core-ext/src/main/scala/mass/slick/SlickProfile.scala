package mass.slick

import java.util.concurrent.TimeUnit

import com.github.tminglei.slickpg._
import helloscala.common.Configuration
import helloscala.common.data.NameValue
import helloscala.common.jackson.Jackson
import helloscala.common.types.ObjectId
import mass.core.jdbc.JdbcUtils
import org.json4s.{JValue, JsonMethods}
import slick.basic.Capability
import slick.jdbc.JdbcCapabilities
import slick.util.AsyncExecutor

import scala.concurrent.duration.FiniteDuration

trait SlickProfile
  extends ExPostgresProfile
  with PgArraySupport
  with PgDate2Support
  with PgJson4sSupport
  with PgHStoreSupport {

  override protected def computeCapabilities: Set[Capability] = super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val pgjson = "jsonb"

  override type DOCType = org.json4s.JValue
  override val jsonMethods: JsonMethods[DOCType] = org.json4s.jackson.JsonMethods.asInstanceOf[JsonMethods[DOCType]]
  override val api: MyAPI.type = MyAPI

  object MyAPI
    extends API
    with ArrayImplicits
    with JsonImplicits
    with DateTimeImplicits
    with HStoreImplicits {

    //    implicit object SetZonedDateTime extends SetParameter[ZonedDateTime] {
    //      override def apply(v1: ZonedDateTime, v2: PositionedParameters): Unit = v2.setTimestamp(Timestamp.from(v1.toInstant))
    //    }
    //
    //    implicit object SetOptionZonedDateTime extends SetParameter[Option[ZonedDateTime]] {
    //      override def apply(v1: Option[ZonedDateTime], v2: PositionedParameters): Unit = v2.setTimestampOption(v1.map(zdt => Timestamp.from(zdt.toInstant)))
    //    }
    //
    //    implicit object SetLocalDateTime extends SetParameter[LocalDateTime] {
    //      override def apply(v1: LocalDateTime, v2: PositionedParameters): Unit = v2.setTimestamp(Timestamp.from(v1.toInstant(TimeUtils.ZONE_CHINA_OFFSET)))
    //    }
    //
    //    implicit object SetOptionLocalDateTime extends SetParameter[Option[LocalDateTime]] {
    //      override def apply(v1: Option[LocalDateTime], v2: PositionedParameters): Unit = v2.setTimestampOption(v1.map(ldt => Timestamp.from(ldt.toInstant(TimeUtils.ZONE_CHINA_OFFSET))))
    //    }
    //
    //    implicit object SetLocalDate extends SetParameter[LocalDate] {
    //      override def apply(v1: LocalDate, v2: PositionedParameters): Unit = v2.setDate(java.sql.Date.valueOf(v1))
    //    }
    //
    //    implicit object SetOptionLocalDate extends SetParameter[Option[LocalDate]] {
    //      override def apply(v1: Option[LocalDate], v2: PositionedParameters): Unit = v2.setDateOption(v1.map(java.sql.Date.valueOf))
    //    }
    //
    //    implicit object SetLocalTime extends SetParameter[LocalTime] {
    //      override def apply(v1: LocalTime, v2: PositionedParameters): Unit = v2.setTime(java.sql.Time.valueOf(v1))
    //    }
    //
    //    implicit object SetOptionLocalTime extends SetParameter[Option[LocalTime]] {
    //      override def apply(v1: Option[LocalTime], v2: PositionedParameters): Unit = v2.setTimeOption(v1.map(java.sql.Time.valueOf))
    //    }

    implicit val durationColumnType: BaseColumnType[FiniteDuration] = MappedColumnType.base[FiniteDuration, Long](_.toMillis, millis => FiniteDuration(millis, TimeUnit.MILLISECONDS))

    implicit val passportColumnType: BaseColumnType[NameValue] = MappedColumnType.base[NameValue, String](
      { node => Jackson.defaultObjectMapper.writeValueAsString(node) }, { str => Jackson.defaultObjectMapper.readValue(str, classOf[NameValue]) })

    //    implicit val jsonNodeColumnType: BaseColumnType[JsonNode] = MappedColumnType.base[JsonNode, JsonString](
    //      { node => JsonString(node.toString) }, { str => Jackson.defaultObjectMapper.readTree(str.value) })

    //    implicit val objectNodeColumnType: BaseColumnType[ObjectNode] = MappedColumnType.base[ObjectNode, JsonString](
    //      { node => JsonString(node.toString) }, { str => Jackson.defaultObjectMapper.readValue(str.value, classOf[ObjectNode]) })

    implicit val objectIdTypeMapper: BaseColumnType[ObjectId] = MappedColumnType.base[ObjectId, String](
      { oid =>
        if (oid eq null) {
          throw new NullPointerException("objectIdTypeMapper: ObjectId is null")
        }
        oid.toString()
      }, { str => ObjectId.apply(str) })

    implicit val strListTypeMapper: DriverJdbcType[List[String]] = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val json4sJsonArrayTypeMapper: AdvancedArrayJdbcType[JValue] = new AdvancedArrayJdbcType[JValue](
      pgjson,
      s => utils.SimpleArrayUtils.fromString[JValue](jsonMethods.parse(_))(s).orNull,
      v => utils.SimpleArrayUtils.mkString[JValue](j => jsonMethods.compact(jsonMethods.render(j)))(v)
    )

    implicit val int4ListTypeMapper: DriverJdbcType[List[Int]] = new SimpleArrayJdbcType[Int]("int4").to(_.toList)
    implicit val int8ListTypeMapper: DriverJdbcType[List[Long]] = new SimpleArrayJdbcType[Long]("int8").to(_.toList)

    //    implicit val objectJsonArrayTypeMapper: DriverJdbcType[List[JsonNode]] =
    //      new AdvancedArrayJdbcType[JsonNode](
    //        pgjson,
    //        (s) => utils.SimpleArrayUtils.fromString[JsonNode](str => Jackson.defaultObjectMapper.readTree(str))(s).orNull,
    //        (v) => utils.SimpleArrayUtils.mkString[JsonNode](node => Jackson.defaultObjectMapper.writeValueAsString(node))(v)).to(_.toList)

    implicit val objectIdListTypeMapper: DriverJdbcType[List[ObjectId]] = new AdvancedArrayJdbcType[ObjectId](
      "text",
      s => utils.SimpleArrayUtils.fromString[ObjectId](str => ObjectId.apply(str))(s).orNull,
      v => utils.SimpleArrayUtils.mkString[ObjectId](id => id.toString())(v)).to(_.toList)

    type FilterCriteriaType = Option[Rep[Option[Boolean]]]

    def dynamicFilter(list: Seq[FilterCriteriaType]): Rep[Option[Boolean]] =
      list.collect({ case Some(criteria) => criteria }).reduceLeftOption(_ && _).getOrElse(Some(true): Rep[Option[Boolean]])

    def dynamicFilter(item: Option[Rep[Boolean]], list: Option[Rep[Boolean]]*): Rep[Boolean] =
      (item +: list).collect({ case Some(criteria) => criteria }).reduceLeftOption(_ && _).getOrElse(true: Rep[Boolean])

    def dynamicFilterOr(list: Seq[FilterCriteriaType]): Rep[Option[Boolean]] =
      list.collect({ case Some(criteria) => criteria }).reduceLeftOption(_ || _).getOrElse(Some(true): Rep[Option[Boolean]])

  }

  trait ColumnOptions extends super.ColumnOptions {
    val SqlTypeObjectId = SqlType("char(24)")

    val SqlTypeSha256 = SqlType("char(64)")
  }

  override val columnOptions: ColumnOptions = new ColumnOptions {}

  def createDatabase(configuration: Configuration): backend.DatabaseDef = {
    val ds = JdbcUtils.createHikariDataSource(configuration)
    val poolName = configuration.getOrElse[String]("poolName", "default")
    val numThreads = configuration.getOrElse[Int]("numThreads", 20)
    val maximumPoolSize = configuration.getOrElse[Int]("maximumPoolSize", numThreads)
    val registerMbeans = configuration.getOrElse[Boolean]("registerMbeans", false)
    val executor = AsyncExecutor(
      poolName,
      numThreads,
      numThreads,
      configuration.getOrElse[Int]("queueSize", 1000),
      maximumPoolSize, registerMbeans = registerMbeans)
    api.Database.forDataSource(ds, Some(maximumPoolSize), executor)
  }
}

object SlickProfile extends SlickProfile
