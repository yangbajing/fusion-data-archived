/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package com.fasterxml.jackson.module.helloscala

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.deser.Deserializers
import com.fasterxml.jackson.databind.ser.Serializers
import helloscala.common.jackson.{ObjectIdDeserializer, ObjectIdSerializer}
import helloscala.common.types.ObjectId

class HelloscalaSerializers extends Serializers.Base {
  override def findSerializer(config: SerializationConfig, `type`: JavaType, beanDesc: BeanDescription): JsonSerializer[_] = {
    val rawClass = `type`.getRawClass
    if (classOf[ObjectId].isAssignableFrom(rawClass)) new ObjectIdSerializer
    else super.findSerializer(config, `type`, beanDesc)
  }
}

class HelloscalaDeserializers extends Deserializers.Base {
  @throws[JsonMappingException]
  override def findBeanDeserializer(`type`: JavaType, config: DeserializationConfig, beanDesc: BeanDescription): JsonDeserializer[_] = {
    val rawClass = `type`.getRawClass
    if (classOf[ObjectId].isAssignableFrom(rawClass)) new ObjectIdDeserializer
    else super.findBeanDeserializer(`type`, config, beanDesc)
  }
}

class HelloscalaModule extends Module {
  override def getModuleName = "helloscala"

  override def version: Version = Version.unknownVersion

  override def setupModule(context: Module.SetupContext): Unit = {
    context.addSerializers(new HelloscalaSerializers)
    context.addDeserializers(new HelloscalaDeserializers)
  }
}
