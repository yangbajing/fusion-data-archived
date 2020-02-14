package mass.model.job

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.{ JsonGenerator, JsonParser }
import com.fasterxml.jackson.databind.{ DeserializationContext, SerializerProvider }
import com.fasterxml.jackson.databind.annotation.{ JsonDeserialize, JsonSerialize }
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import helloscala.common.data.StringValueName

@JsonSerialize(using = classOf[Program.EnumSer])
@JsonDeserialize(using = classOf[Program.EnumDeser])
sealed abstract class Program(@JsonValue val value: String, val name: String) {
  def toValueName: StringValueName = StringValueName(value, name)
}

object Program {
  case object SCALA extends Program("scala", "Scala")
  case object JAVA extends Program("java", "Java")
  case object PYTHON extends Program("python", "Python")
  case object SH extends Program("sh", "SH")
  case object SQL extends Program("sql", "SQL")
  case object JS extends Program("js", "Javascript")

  val values = Vector(SCALA, JAVA, PYTHON, SH, SQL, JS)

  def fromValue(value: String): Program =
    optionFromValue(value).getOrElse(
      throw new NoSuchElementException(s"Program.values by name not found, it is $value."))

  def optionFromValue(value: String): Option[Program] = {
    val v = value.toLowerCase()
    values.find(_.value == v)
  }

  def fromName(name: String): Program =
    optionFromName(name).getOrElse(throw new NoSuchElementException(s"Program.values by name not found, it is $name."))

  def optionFromName(name: String): Option[Program] = {
    val n = name.toLowerCase()
    values.find(_.name == n)
  }

  class EnumSer extends StdSerializer[Program](classOf[Program]) {
    override def serialize(value: Program, gen: JsonGenerator, provider: SerializerProvider): Unit =
      gen.writeString(value.value)
  }
  class EnumDeser extends StdDeserializer[Program](classOf[Program]) {
    override def deserialize(p: JsonParser, ctxt: DeserializationContext): Program =
      Program.fromValue(p.getValueAsString)
  }
}
