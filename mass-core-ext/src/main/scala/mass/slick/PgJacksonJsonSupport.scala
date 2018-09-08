package mass.slick

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.github.tminglei.slickpg.json.PgJsonExtensions
import com.github.tminglei.slickpg.utils.{PgCommonJdbcTypes, SimpleArrayUtils}
import com.github.tminglei.slickpg.{ArraySupport, ExPostgresProfile}
import helloscala.common.jackson.Jackson
import helloscala.data.NameValue
import mass.model.job.{JobItem, JobTrigger}
import slick.jdbc._

import scala.language.implicitConversions
import scala.reflect.{classTag, ClassTag}
import scala.util.Try

trait PgJacksonJsonSupport extends PgJsonExtensions with PgCommonJdbcTypes {
  driver: PostgresProfile with ArraySupport =>
  import driver.api._

  def pgjson: String

  trait JacksonCodeGenSupport {
    driver match {
      case profile: ExPostgresProfile =>
        profile.bindPgTypeToScala("json", classTag[JsonNode])
        profile.bindPgTypeToScala("jsonb", classTag[JsonNode])
      case _ =>
    }
  }

  trait JsonImplicits extends JacksonImplicits

  trait JacksonImplicits extends JacksonCodeGenSupport {
    implicit val jacksonJsonTypeMapper: JdbcType[JsonNode] = new GenericJdbcType[JsonNode](
      pgjson,
      v => Try(Jackson.defaultObjectMapper.readTree(v)).getOrElse(NullNode.instance),
      v => Jackson.defaultObjectMapper.writeValueAsString(v))

    implicit val jacksonArrayTypeMapper: AdvancedArrayJdbcType[JsonNode] =
      new AdvancedArrayJdbcType[JsonNode](
        pgjson,
        s => SimpleArrayUtils.fromString[JsonNode](jstr => Jackson.defaultObjectMapper.readTree(jstr))(s).orNull,
        v => SimpleArrayUtils.mkString[JsonNode](jnode => Jackson.defaultObjectMapper.writeValueAsString(jnode))(v)
      )

    implicit val passportColumnType: JdbcType[NameValue] = mkJsonColumnType[NameValue]
    implicit val triggerConfTypeMapper: JdbcType[JobTrigger] = mkJsonColumnType[JobTrigger]
    implicit val jobItemTypeMapper: JdbcType[JobItem] = mkJsonColumnType[JobItem]

    def mkJsonColumnType[T](implicit ev1: ClassTag[T]): JdbcType[T] =
      MappedColumnType.base[T, JsonNode](Jackson.defaultObjectMapper.valueToTree,
                                         Jackson.defaultObjectMapper.treeToValue(_, ev1.runtimeClass).asInstanceOf[T])

    implicit def jacksonJsonColumnExtensionMethods(c: Rep[JsonNode]): JsonColumnExtensionMethods[JsonNode, JsonNode] =
      new JsonColumnExtensionMethods[JsonNode, JsonNode](c)

    implicit def jacksonJsonOptionColumnExtensionMethods(
        c: Rep[Option[JsonNode]]): JsonColumnExtensionMethods[JsonNode, Option[JsonNode]] =
      new JsonColumnExtensionMethods[JsonNode, Option[JsonNode]](c)
  }

  trait JacksonJsonPlainImplicits extends JacksonCodeGenSupport {
    import com.github.tminglei.slickpg.utils.PlainSQLUtils._
    implicit class PgJacksonJsonPositionResult(r: PositionedResult) {
      def nextJson(): JsonNode = nextJsonOption().getOrElse(NullNode.instance)

      def nextJsonOption(): Option[JsonNode] =
        r.nextStringOption().map(s => Try(Jackson.defaultObjectMapper.readTree(s)).getOrElse(NullNode.instance))
    }

    implicit val getJacksonJson: GetResult[JsonNode] = mkGetResult(_.nextJson())
    implicit val getJacksonJsonOption: GetResult[Option[JsonNode]] = mkGetResult(_.nextJsonOption())
    implicit val setJacksonJson: SetParameter[JsonNode] =
      mkSetParameter(pgjson, jnode => Jackson.defaultObjectMapper.writeValueAsString(jnode))
    implicit val setJacksonJsonOption: SetParameter[Option[JsonNode]] =
      mkOptionSetParameter[JsonNode](pgjson, jnode => Jackson.defaultObjectMapper.writeValueAsString(jnode))
  }
}
