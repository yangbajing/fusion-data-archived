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

package helloscala.common.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.{JsonParser, JsonProcessingException, TreeNode}
import com.fasterxml.jackson.databind._
import com.fasterxml.jackson.databind.introspect.{Annotated, JacksonAnnotationIntrospector}
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.fasterxml.jackson.databind.ser.impl.{SimpleBeanPropertyFilter, SimpleFilterProvider}
import com.fasterxml.jackson.databind.ser.{DefaultSerializerProvider, SerializerFactory}
import helloscala.common.exception.HSBadRequestException
import helloscala.common.util.Utils
import scalapb.GeneratedMessage

import scala.reflect.ClassTag

object Jackson {
  val defaultObjectMapper: ObjectMapper = createObjectMapper

  def createObjectNode: ObjectNode = defaultObjectMapper.createObjectNode

  def createArrayNode: ArrayNode = defaultObjectMapper.createArrayNode

  def readTree(jstr: String): JsonNode = defaultObjectMapper.readTree(jstr)

  def valueToTree(v: AnyRef): JsonNode = defaultObjectMapper.valueToTree(v)

  def treeToValue[T](tree: TreeNode)(implicit ev1: ClassTag[T]): T =
    defaultObjectMapper.treeToValue(tree, ev1.runtimeClass).asInstanceOf[T]

  def stringify(v: AnyRef): String = defaultObjectMapper.writeValueAsString(v)

  def prettyStringify(v: AnyRef): String = defaultObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(v)

  def extract[T](tree: TreeNode)(implicit ev1: ClassTag[T]): Either[JsonProcessingException, T] = Utils.either {
    defaultObjectMapper.treeToValue(tree, ev1.runtimeClass).asInstanceOf[T]
  }

  @inline def extract[T](compare: Boolean, tree: TreeNode)(implicit ev1: ClassTag[T]): Either[Throwable, T] =
    if (compare) extract(tree) else Left(HSBadRequestException(s"compare比较结果为false，需要类型：${ev1.runtimeClass.getName}"))

  private def createObjectMapper: ObjectMapper = {
    val FILTER_ID_CLASS: Class[GeneratedMessage] = classOf[GeneratedMessage]
    new ObjectMapper()
      .setFilterProvider(new SimpleFilterProvider()
        .addFilter(FILTER_ID_CLASS.getName, SimpleBeanPropertyFilter.serializeAllExcept("allFields")))
      .setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
        override def findFilterId(a: Annotated): AnyRef =
          if (FILTER_ID_CLASS.isAssignableFrom(a.getRawType)) FILTER_ID_CLASS.getName else super.findFilterId(a)
      })
      .findAndRegisterModules()
      .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
      .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
      .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)
      .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
//      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
//      .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
      .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    //      .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
    //                    .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
  }

  private class MassSerializerProvider(src: SerializerProvider, config: SerializationConfig, f: SerializerFactory)
      extends DefaultSerializerProvider(src, config, f) {
    def this() {
      this(null, null, null)
    }

    def this(src: MassSerializerProvider) {
      this(src, null, null)
    }

    override def copy: DefaultSerializerProvider = {
      if (getClass ne classOf[MassSerializerProvider]) return super.copy
      new MassSerializerProvider(this)
    }

    override def createInstance(config: SerializationConfig, jsf: SerializerFactory) =
      new MassSerializerProvider(this, config, jsf)
  }

}
