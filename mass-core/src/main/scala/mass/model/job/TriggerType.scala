package mass.model.job

import com.fasterxml.jackson.core.{ JsonGenerator, JsonParser }
import com.fasterxml.jackson.databind.annotation.{ JsonDeserialize, JsonSerialize }
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.{ DeserializationContext, SerializerProvider }
import helloscala.common.util.{ IEnumTrait, IEnumTraitCompanion }

@JsonSerialize(using = classOf[TriggerType.EnumSer])
@JsonDeserialize(using = classOf[TriggerType.EnumDeser])
sealed trait TriggerType extends IEnumTrait[String]

object TriggerType extends IEnumTraitCompanion[String] {
  self =>
  override type Value = TriggerType

  case object SIMPLE extends TriggerType {
    override val companion: IEnumTraitCompanion[String] = self
    override val value: String = "SIMPLE"
  }
  case object CRON extends TriggerType {
    override val companion: IEnumTraitCompanion[String] = self
    override val value: String = "CRON"
  }
  case object EVENT extends TriggerType {
    override val companion: IEnumTraitCompanion[String] = self
    override val value: String = "EVENT"
  }

  override val values = Vector(CRON, EVENT, SIMPLE)

  class EnumSer extends StdSerializer[TriggerType](classOf[TriggerType]) {
    override def serialize(value: TriggerType, gen: JsonGenerator, provider: SerializerProvider): Unit =
      gen.writeString(value.value)
  }
  class EnumDeser extends StdDeserializer[TriggerType](classOf[TriggerType]) {
    override def deserialize(p: JsonParser, ctxt: DeserializationContext): TriggerType =
      TriggerType.fromValue(p.getValueAsString.toUpperCase())
  }
}
