package mass.connector.sql

import java.nio.charset.{Charset, StandardCharsets}
import java.sql.ResultSet

import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import mass.core.jdbc.JdbcUtils

import scala.collection.immutable

case class JdbcResultSet(rs: ResultSet, values: immutable.IndexedSeq[AnyRef])

object JdbcFlow {

  def flowToText(valueSeparator: Char = ','): Flow[immutable.IndexedSeq[AnyRef], String, NotUsed] =
    Flow[immutable.IndexedSeq[AnyRef]].map { values =>
      val builder = new java.lang.StringBuilder()
      var i = 0
      while (i < values.length) {
        builder.append(values(i).toString)
        i += 1
        if (i < values.length) {
          builder.append(valueSeparator)
        }
      }
      builder.toString
    }

  def flowToSeq: Flow[ResultSet, immutable.IndexedSeq[AnyRef], NotUsed] =
    Flow[ResultSet].map { rs =>
      val metaData = rs.getMetaData
      (1 to rs.getMetaData.getColumnCount).map { i =>
        val typ = metaData.getColumnType(i)
        if (JdbcUtils.isString(typ)) {
          rs.getString(i)
        } else
          rs.getObject(i)
      }
    }

  def flowToByteString(valueSeparator: Char = ',', charset: Charset = StandardCharsets.UTF_8): Flow[immutable.IndexedSeq[AnyRef], ByteString, NotUsed] =
    Flow[immutable.IndexedSeq[AnyRef]].map { values =>
      val builder = ByteString.newBuilder
      var i = 0
      while (i < values.length) {
        builder.putBytes(values(i).toString.getBytes(charset))
        i += 1
        if (i < values.length) {
          builder.putByte(valueSeparator.toByte)
        }
      }
      builder.result()
    }

  def flowJdbcResultSet: Flow[ResultSet, JdbcResultSet, NotUsed] = Flow[ResultSet].map { rs =>
    val metaData = rs.getMetaData
    JdbcResultSet(rs, (1 to metaData.getColumnCount).map(i => rs.getObject(i)))
  }

}
