/*
 * Copyright 2018 羊八井(yangbajing)（杨景）
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fasterxml.jackson.module.helloscala

import java.util.concurrent.TimeUnit

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser, Version}
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.ser.Serializers
import helloscala.common.jackson.{ObjectIdDeserializer, ObjectIdSerializer}
import helloscala.common.types.ObjectId
import helloscala.common.util.StringUtils

import scala.concurrent.duration.FiniteDuration

class FiniteDurationDeserializer extends JsonDeserializer[FiniteDuration] {
  override def deserialize(p: JsonParser, ctxt: DeserializationContext): FiniteDuration = {
    p.getValueAsLong(-1) match {
      case -1 =>
        p.getValueAsString match {
          case null => ctxt.handleUnexpectedToken(classOf[FiniteDuration], p).asInstanceOf[FiniteDuration]
          case str  => FiniteDuration(scala.concurrent.duration.Duration(str).toMillis, TimeUnit.MILLISECONDS)
        }
      case millis => FiniteDuration(millis, TimeUnit.MILLISECONDS)
    }
  }
}

class FiniteDurationSerializer extends JsonSerializer[FiniteDuration] {
  override def serialize(value: FiniteDuration, gen: JsonGenerator, serializers: SerializerProvider): Unit =
    gen.writeNumber(value.toMillis)
}

class HelloscalaSerializers extends Serializers.Base {
  override def findSerializer(
      config: SerializationConfig,
      `type`: JavaType,
      beanDesc: BeanDescription): JsonSerializer[_] = {
    val rawClass = `type`.getRawClass
    if (classOf[ObjectId].isAssignableFrom(rawClass)) new ObjectIdSerializer
    if (classOf[FiniteDuration].isAssignableFrom(rawClass)) new FiniteDurationSerializer
    else super.findSerializer(config, `type`, beanDesc)
  }
}

class HelloscalaDeserializers extends Deserializers.Base {

  @throws[JsonMappingException]
  override def findBeanDeserializer(
      `type`: JavaType,
      config: DeserializationConfig,
      beanDesc: BeanDescription
  ): JsonDeserializer[_] = {
    val rawClass = `type`.getRawClass
    if (classOf[ObjectId].isAssignableFrom(rawClass)) new ObjectIdDeserializer
    else if (classOf[FiniteDuration].isAssignableFrom(rawClass)) new FiniteDurationDeserializer
    else super.findBeanDeserializer(`type`, config, beanDesc)
  }
}

class HelloscalaModule extends Module {
  override def getModuleName = "helloscala"

  override def version: Version = Version.unknownVersion

  override def setupModule(context: Module.SetupContext): Unit = {
    context.addSerializers(new HelloscalaSerializers)
    context.addDeserializers(new HelloscalaDeserializers)
//    context.addKeySerializers()
  }
}
