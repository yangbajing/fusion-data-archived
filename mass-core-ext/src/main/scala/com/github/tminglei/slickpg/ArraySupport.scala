package com.github.tminglei.slickpg

import java.sql.{Date, Time, Timestamp}
import java.util.UUID

import com.github.tminglei.slickpg.array.{PgArrayExtensions, PgArrayJdbcTypes}
import com.github.tminglei.slickpg.utils.SimpleArrayUtils
import helloscala.common.types.ObjectId
import slick.jdbc._

import scala.language.{higherKinds, implicitConversions}
import scala.reflect.classTag
import scala.reflect.runtime.{universe => u}

trait ArraySupport extends PgArrayExtensions with PgArrayJdbcTypes { driver: PostgresProfile =>

  import driver.api._

  trait SimpleArrayCodeGenSupport {
    // register types to let `ExModelBuilder` find them
    if (driver.isInstanceOf[ExPostgresProfile]) {
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_uuid", classTag[Seq[UUID]])
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_text", classTag[Seq[String]])
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_int8", classTag[Seq[Long]])
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_int4", classTag[Seq[Int]])
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_int2", classTag[Seq[Short]])
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_float4", classTag[Seq[Float]])
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_float8", classTag[Seq[Double]])
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_bool", classTag[Seq[Boolean]])
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_date", classTag[Seq[Date]])
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_time", classTag[Seq[Time]])
      driver.asInstanceOf[ExPostgresProfile].bindPgTypeToScala("_timestamp", classTag[Seq[Timestamp]])
    }
  }

  /// static sql support, NOTE: no extension methods available for static sql usage
  trait SimpleArrayPlainImplicits extends SimpleArrayCodeGenSupport {
    import com.github.tminglei.slickpg.utils.PlainSQLUtils._
    // to support 'nextArray[T]/nextArrayOption[T]' in PgArraySupport
    {
      //      addNextArrayConverter((r) => simpleNextArray[Int](r).map(_.map(_.toShort)))
    }

    implicit class PgArrayPositionedResult(r: PositionedResult) {
      def nextArray[T]()(implicit tpe: u.TypeTag[T]): Seq[T] = nextArrayOption[T]().getOrElse(Nil)

      def nextArrayOption[T]()(implicit ttag: u.TypeTag[T]): Option[Seq[T]] =
        nextArrayConverters
          .get(u.typeOf[T].toString)
          .map(_.apply(r))
          .getOrElse(simpleNextArray[T](r))
          .asInstanceOf[Option[Seq[T]]]
    }

    private def simpleNextArray[T](r: PositionedResult): Option[Seq[T]] = {
      val value = r.rs.getArray(r.skip.currentPos)
      if (r.rs.wasNull) None else Some(value.getArray.asInstanceOf[Array[Any]].map(_.asInstanceOf[T]))
    }

    //////////////////////////////////////////////////////////////////////////
    implicit val getUUIDArray: GetResult[Seq[UUID]] = mkGetResult(_.nextArray[UUID]())
    implicit val getUUIDArrayOption: GetResult[Option[Seq[UUID]]] = mkGetResult(_.nextArrayOption[UUID]())
    implicit val setUUIDArray: SetParameter[Seq[UUID]] = mkArraySetParameter[UUID]("uuid")
    implicit val setUUIDArrayOption: SetParameter[Option[Seq[UUID]]] = mkArrayOptionSetParameter[UUID]("uuid")
    ///
    implicit val getStringArray: GetResult[Seq[String]] = mkGetResult(_.nextArray[String]())
    implicit val getStringArrayOption: GetResult[Option[Seq[String]]] = mkGetResult(_.nextArrayOption[String]())
    implicit val setStringArray: SetParameter[Seq[String]] = mkArraySetParameter[String]("text")
    implicit val setStringArrayOption: SetParameter[Option[Seq[String]]] = mkArrayOptionSetParameter[String]("text")
    ///
    implicit val getLongArray: GetResult[Seq[Long]] = mkGetResult(_.nextArray[Long]())
    implicit val getLongArrayOption: GetResult[Option[Seq[Long]]] = mkGetResult(_.nextArrayOption[Long]())
    implicit val setLongArray: SetParameter[Seq[Long]] = mkArraySetParameter[Long]("int8")
    implicit val setLongArrayOption: SetParameter[Option[Seq[Long]]] = mkArrayOptionSetParameter[Long]("int8")
    ///
    implicit val getIntArray: GetResult[Seq[Int]] = mkGetResult(_.nextArray[Int]())
    implicit val getIntArrayOption: GetResult[Option[Seq[Int]]] = mkGetResult(_.nextArrayOption[Int]())
    implicit val setIntArray: SetParameter[Seq[Int]] = mkArraySetParameter[Int]("int4")
    implicit val setIntArrayOption: SetParameter[Option[Seq[Int]]] = mkArrayOptionSetParameter[Int]("int4")
    ///
    implicit val getShortArray: GetResult[Seq[Short]] = mkGetResult(_.nextArray[Short]())
    implicit val getShortArrayOption: GetResult[Option[Seq[Short]]] = mkGetResult(_.nextArrayOption[Short]())
    implicit val setShortArray: SetParameter[Seq[Short]] = mkArraySetParameter[Short]("int2")
    implicit val setShortArrayOption: SetParameter[Option[Seq[Short]]] = mkArrayOptionSetParameter[Short]("int2")
    ///
    implicit val getFloatArray: GetResult[Seq[Float]] = mkGetResult(_.nextArray[Float]())
    implicit val getFloatArrayOption: GetResult[Option[Seq[Float]]] = mkGetResult(_.nextArrayOption[Float]())
    implicit val setFloatArray: SetParameter[Seq[Float]] = mkArraySetParameter[Float]("float4")
    implicit val setFloatArrayOption: SetParameter[Option[Seq[Float]]] = mkArrayOptionSetParameter[Float]("float4")
    ///
    implicit val getDoubleArray: GetResult[Seq[Double]] = mkGetResult(_.nextArray[Double]())
    implicit val getDoubleArrayOption: GetResult[Option[Seq[Double]]] = mkGetResult(_.nextArrayOption[Double]())
    implicit val setDoubleArray: SetParameter[Seq[Double]] = mkArraySetParameter[Double]("float8")
    implicit val setDoubleArrayOption: SetParameter[Option[Seq[Double]]] = mkArrayOptionSetParameter[Double]("float8")
    ///
    implicit val getBoolArray: GetResult[Seq[Boolean]] = mkGetResult(_.nextArray[Boolean]())
    implicit val getBoolArrayOption: GetResult[Option[Seq[Boolean]]] = mkGetResult(_.nextArrayOption[Boolean]())
    implicit val setBoolArray: SetParameter[Seq[Boolean]] = mkArraySetParameter[Boolean]("bool")
    implicit val setBoolArrayOption: SetParameter[Option[Seq[Boolean]]] = mkArrayOptionSetParameter[Boolean]("bool")
    ///
    implicit val getDateArray: GetResult[Seq[Date]] = mkGetResult(_.nextArray[Date]())
    implicit val getDateArrayOption: GetResult[Option[Seq[Date]]] = mkGetResult(_.nextArrayOption[Date]())
    implicit val setDateArray: SetParameter[Seq[Date]] = mkArraySetParameter[Date]("date")
    implicit val setDateArrayOption: SetParameter[Option[Seq[Date]]] = mkArrayOptionSetParameter[Date]("date")
    ///
    implicit val getTimeArray: GetResult[Seq[Time]] = mkGetResult(_.nextArray[Time]())
    implicit val getTimeArrayOption: GetResult[Option[Seq[Time]]] = mkGetResult(_.nextArrayOption[Time]())
    implicit val setTimeArray: SetParameter[Seq[Time]] = mkArraySetParameter[Time]("time")
    implicit val setTimeArrayOption: SetParameter[Option[Seq[Time]]] = mkArrayOptionSetParameter[Time]("time")
    ///
    implicit val getTimestampArray: GetResult[Seq[Timestamp]] = mkGetResult(_.nextArray[Timestamp]())
    implicit val getTimestampArrayOption: GetResult[Option[Seq[Timestamp]]] = mkGetResult(
      _.nextArrayOption[Timestamp]())
    implicit val setTimestampArray: SetParameter[Seq[Timestamp]] = mkArraySetParameter[Timestamp]("timestamp")
    implicit val setTimestampArrayOption: SetParameter[Option[Seq[Timestamp]]] =
      mkArrayOptionSetParameter[Timestamp]("timestamp")

    implicit val getObjectIdArray: GetResult[Seq[ObjectId]] = mkGetResult(_.nextArray[String]().map(ObjectId.apply))
    implicit val getObjectIdArrayOption: GetResult[Option[Seq[ObjectId]]] = mkGetResult(
      _.nextArrayOption[String]().map(_.map(ObjectId.apply)))
    implicit val setObjectIdArray: SetParameter[Seq[ObjectId]] = mkArraySetParameter("text", v => v.stringify)
    implicit val setObjectIdArrayOption: SetParameter[Option[Seq[ObjectId]]] =
      mkArrayOptionSetParameter("text", v => v.stringify)
  }

  trait ArrayImplicits extends SimpleArrayCodeGenSupport {

    /** for type/name, @see [[org.postgresql.core.Oid]] and [[org.postgresql.jdbc.TypeInfoCache]]*/
    implicit val simpleUUIDListTypeMapper: JdbcType[Seq[UUID]] = new SimpleArrayJdbcType[UUID]("uuid")
    implicit val simpleStrSeqTypeMapper: JdbcType[Seq[String]] = new SimpleArrayJdbcType[String]("text")
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
        c: Rep[SEQ[B1]]
    )(implicit tm: JdbcType[B1], tm1: JdbcType[SEQ[B1]]): ArrayColumnExtensionMethods[B1, SEQ, SEQ[B1]] =
      new ArrayColumnExtensionMethods[B1, SEQ, SEQ[B1]](c)

    implicit def simpleArrayOptionColumnExtensionMethods[B1, SEQ[B1] <: Seq[B1]](
        c: Rep[Option[SEQ[B1]]]
    )(implicit tm: JdbcType[B1], tm1: JdbcType[SEQ[B1]]): ArrayColumnExtensionMethods[B1, SEQ, Option[SEQ[B1]]] =
      new ArrayColumnExtensionMethods[B1, SEQ, Option[SEQ[B1]]](c)

    /// custom array mapper
    implicit val objectIdListTypeMapper: AdvancedArrayJdbcType[ObjectId] =
      new AdvancedArrayJdbcType[ObjectId](
        "text",
        s => SimpleArrayUtils.fromString[ObjectId](ObjectId.apply)(s).orNull,
        v => SimpleArrayUtils.mkString[ObjectId](_.toString())(v)
      )

  }

}
