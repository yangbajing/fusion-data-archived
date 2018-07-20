package example

import java.nio.file.Files
import java.time.OffsetDateTime

import helloscala.common.jackson.Jackson
import helloscala.common.test.HelloscalaSpec
import helloscala.common.util.TimeUtils
import mass.core.json.{Json4sFormats, Json4sMethods}
import org.json4s.Extraction

case class Item(name: String, createdAt: OffsetDateTime)

class Demo2 extends HelloscalaSpec {

  "temp file" in {
    val p = Files.createTempFile("mass-", ".tmp")
    println(p)
  }

  "offset-datetime" in {
    val odt = OffsetDateTime.now()
    println(odt)
    val str = odt.toString
    println(TimeUtils.toOffsetDateTime(str))
  }

  "jvalue-jsonnode" in {
    implicit val formats = Json4sFormats
    val item = Item("羊八井", OffsetDateTime.now())
    val jvalue = Extraction.decompose(item)
    println(jvalue)
    println(jvalue.getClass)
    val jsonnode = Json4sMethods.asJsonNode(jvalue)
    println(jsonnode)

    val str = Jackson.defaultObjectMapper.writeValueAsString(item)
    println(str)
  }

  "2dim array" in {
    val arrArr: Array[Array[Int]] = Array.fill(3, 4)(0)

    System.nanoTime()
  }

  "csvread" in {
    val arr2: Array[Int] = scala.io.Source.fromResource("word.csv").getLines().flatMap(str => str.split(',').map(_.toInt)).toArray
  }
}
