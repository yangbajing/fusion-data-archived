package mass.core.json

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.ser.Serializers
import com.fasterxml.jackson.module.scala.JacksonModule
import mass.core.ScalaPBProtos
import scalapb.GeneratedEnum

abstract private class ScalaPBEnumDeser[T](clz: Class[T]) extends JsonDeserializer[T] {
  override def deserialize(jp: JsonParser, ctxt: DeserializationContext): T = {
    val clzName = clz.getName
    ScalaPBProtos.enumerationCompanions.get(clzName) match {
      case Some(companion) =>
        val i = jp.getValueAsInt()
        companion.fromValue(i).asInstanceOf[T]
      case None =>
        ctxt.handleUnexpectedToken(clz, jp).asInstanceOf[T]
    }
  }
}

private object ScalaPBJacksonDeserializerResolver extends Deserializers.Base {

  object CommonStatusDeser extends ScalaPBEnumDeser(ClassCommonStatus)
  object ProgramDeser extends ScalaPBEnumDeser(ClassProgram)
  object TriggerTypeDeser extends ScalaPBEnumDeser(ClassTriggerType)

  override def findBeanDeserializer(
      javaType: JavaType,
      config: DeserializationConfig,
      beanDesc: BeanDescription): JsonDeserializer[_] = {
    val clazz = javaType.getRawClass

    if (ClassCommonStatus.isAssignableFrom(clazz)) CommonStatusDeser
    else if (ClassProgram.isAssignableFrom(clazz)) ProgramDeser
    else if (ClassTriggerType.isAssignableFrom(clazz)) TriggerTypeDeser
    else super.findBeanDeserializer(javaType, config, beanDesc)
  }
}

private class ScalaPBEnumSerializer extends JsonSerializer[scalapb.GeneratedEnum] {
  override def serialize(value: GeneratedEnum, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
    gen.writeNumber(value.value)
  }
}

private object ScalaPBJacksonSerializerResolver extends Serializers.Base {
  override def findSerializer(
      config: SerializationConfig,
      javaType: JavaType,
      beanDesc: BeanDescription): JsonSerializer[_] = {
    val clazz = javaType.getRawClass
    if (ClassGeneratedEnum.isAssignableFrom(clazz)) {
      new ScalaPBEnumSerializer
    } else {
      super.findSerializer(config, javaType, beanDesc)
    }
  }

}

trait ScalaPBJacksonModule extends JacksonModule {
  this += ScalaPBJacksonSerializerResolver
  this += { ctx =>
    ctx.addDeserializers(ScalaPBJacksonDeserializerResolver)
  }
}
