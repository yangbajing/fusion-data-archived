package mass.db.slick

import java.time.Duration
import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.databind.JsonNode
import com.github.tminglei.slickpg._
import com.github.tminglei.slickpg.agg.PgAggFuncSupport
import com.github.tminglei.slickpg.str.PgStringSupport
import com.zaxxer.hikari.HikariDataSource
import fusion.json.jackson.Jackson
import helloscala.common.data.NameValue
import helloscala.common.types.ObjectId
import mass.model.CommonStatus
import mass.model.job._
import slick.ast.TypedType
import slick.basic.Capability
import slick.jdbc.{ GetResult, JdbcCapabilities, JdbcType, SetParameter }

import scala.concurrent.duration.FiniteDuration

trait PgProfile
    extends ExPostgresProfile
    with PgAggFuncSupport
    with PgDate2Support
    with PgHStoreSupport
    with ArraySupport
    with PgJacksonJsonSupport
    with PgStringSupport {
  override protected def computeCapabilities: Set[Capability] =
    super.computeCapabilities + JdbcCapabilities.insertOrUpdate

  override val pgjson = "jsonb"

  val plainApi: PlainAPI = new PlainAPI {}

  trait PlainAPI
      extends Date2DateTimePlainImplicits
      with JacksonJsonPlainImplicits
      with SimpleHStorePlainImplicits
      with SimpleArrayPlainImplicits
      with ByteaPlainImplicits {
    import com.github.tminglei.slickpg.utils.PlainSQLUtils._

    implicit val getCommonStatus: GetResult[CommonStatus] = mkGetResult(pr => CommonStatus.fromValue(pr.nextInt()))
    implicit val getCommonStatusOption: GetResult[Option[CommonStatus]] = mkGetResult(
      _.nextIntOption().map(CommonStatus.fromValue))
    implicit val setCommonStatus: SetParameter[CommonStatus] = (v, rp) => rp.setInt(v.getValue)
    implicit val setCommonStatusOption: SetParameter[Option[CommonStatus]] = (v, rp) =>
      rp.setIntOption(v.map(_.getValue))

    implicit val getRunStatus: GetResult[RunStatus] = mkGetResult(pr => RunStatus.fromValue(pr.nextInt()))
    implicit val getRunStatusOption: GetResult[Option[RunStatus]] = mkGetResult(
      _.nextIntOption().map(RunStatus.fromValue))
    implicit val setRunStatus: SetParameter[RunStatus] = (v, rp) => rp.setInt(v.getValue)
    implicit val setRunStatusOption: SetParameter[Option[RunStatus]] = (v, rp) => rp.setIntOption(v.map(_.getValue))

    implicit val getTriggerType: GetResult[TriggerType] = mkGetResult(pr => TriggerType.fromValue(pr.nextString()))
    implicit val getTriggerTypeOption: GetResult[Option[TriggerType]] = mkGetResult(
      pr => pr.nextStringOption().map(TriggerType.fromValue))
    implicit val setTriggerType: SetParameter[TriggerType] = (v, rp) => rp.setString(v.value)
    implicit val setTriggerTypeOption: SetParameter[Option[TriggerType]] = (v, rp) => rp.setStringOption(v.map(_.value))

    implicit val getFiniteDuration: GetResult[FiniteDuration] =
      mkGetResult(pr => FiniteDuration(pr.nextDuration().toNanos, TimeUnit.NANOSECONDS).toCoarsest)
    implicit val getFiniteDurationOption: GetResult[Option[FiniteDuration]] =
      mkGetResult(pr => pr.nextDurationOption().map(d => FiniteDuration(d.toNanos, TimeUnit.NANOSECONDS).toCoarsest))

    implicit val setFiniteDuration: SetParameter[FiniteDuration] =
      mkSetParameter[FiniteDuration]("interval", fd => Duration.ofNanos(fd.toNanos).toString)
    implicit val setFiniteDurationOption: SetParameter[Option[FiniteDuration]] =
      mkOptionSetParameter[FiniteDuration]("interval", fd => Duration.ofNanos(fd.toNanos).toString)

    implicit val getObjectId: GetResult[ObjectId] = mkGetResult(pr => ObjectId(pr.nextString()))
    implicit val getObjectIdOption: GetResult[Option[ObjectId]] = mkGetResult(
      pr => pr.nextStringOption().map(ObjectId.apply))
    implicit val setObjectId: SetParameter[ObjectId] = (v, rp) => rp.setString(v.stringify)
    implicit val setObjectIdOption: SetParameter[Option[ObjectId]] = (v, rp) => rp.setStringOption(v.map(_.stringify))

    implicit val getNameValue: GetResult[NameValue] = mkGetResult(
      pr => Jackson.defaultObjectMapper.treeToValue(pr.nextJson(), classOf[NameValue]))
    implicit val getNameValueOption: GetResult[Option[NameValue]] = mkGetResult(
      pr => pr.nextJsonOption().map(Jackson.defaultObjectMapper.treeToValue(_, classOf[NameValue])))
    implicit val setNameValue: SetParameter[NameValue] =
      mkSetParameter(pgjson, v => Jackson.defaultObjectMapper.writeValueAsString(v))
    implicit val setNameValueOption: SetParameter[Option[NameValue]] =
      mkOptionSetParameter(pgjson, v => Jackson.defaultObjectMapper.writeValueAsString(v))
  }

  override val api: API = new API {}

  trait API
      extends super.API
//      with GeneralAggFunctions
      with JsonImplicits
      with HStoreImplicits
      with ArrayImplicits
      with DateTimeImplicits
      with PgStringImplicits {
    val coalesceString: Seq[Rep[_]] => Rep[String] = SimpleFunction("coalesce")
    val coalesceInt: Seq[Rep[_]] => Rep[Int] = SimpleFunction("coalesce")
    val coalesceLong: Seq[Rep[_]] => Rep[Long] = SimpleFunction("coalesce")

    def coalesce[R: TypedType]: Seq[Rep[_]] => Rep[R] = SimpleFunction("coalesce")

    implicit val programColumnType: JdbcType[Program] =
      MappedColumnType.base[Program, String](_.value, Program.fromValue)
    implicit val commonStatusColumnType: JdbcType[CommonStatus] =
      MappedColumnType.base[CommonStatus, Int](_.getValue, CommonStatus.fromValue)
    implicit val durationColumnType: JdbcType[FiniteDuration] =
      MappedColumnType.base[FiniteDuration, Long](_.toMillis, millis => FiniteDuration(millis, TimeUnit.MILLISECONDS))
    implicit val triggerTypeColumnType: JdbcType[TriggerType] =
      MappedColumnType.base[TriggerType, String](_.value, TriggerType.fromValue)
    implicit val jobStatusColumnType: JdbcType[RunStatus] =
      MappedColumnType.base[RunStatus, Int](_.getValue, RunStatus.fromValue)
    implicit val jobItemColumnType: JdbcType[JobItem] =
      MappedColumnType.base[JobItem, JsonNode](Jackson.valueToTree, Jackson.treeToValue[JobItem])
    implicit val jobTriggerColumnType: JdbcType[JobTrigger] =
      MappedColumnType.base[JobTrigger, JsonNode](Jackson.valueToTree, Jackson.treeToValue[JobTrigger])
    implicit val objectIdColumnType: JdbcType[ObjectId] =
      MappedColumnType.base[ObjectId, String](_.toString(), ObjectId.apply)

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

    def databaseForDataSource(dataSource: HikariDataSource): backend.DatabaseDef = {
      Database.forDataSource(
        dataSource,
        None,
        AsyncExecutor(
          dataSource.getPoolName,
          dataSource.getMaximumPoolSize,
          dataSource.getMaximumPoolSize,
          dataSource.getMaximumPoolSize * 2,
          dataSource.getMaximumPoolSize))
    }
  }

  trait AggImplicits {
    this: ArraySupport =>
  }

  trait ColumnOptions extends super.ColumnOptions {
    val SqlTypeObjectId = SqlType("char(24)")
    val SqlTypeSha256 = SqlType("char(64)")
  }

  override val columnOptions: ColumnOptions = new ColumnOptions {}
}

object PgProfile extends PgProfile
