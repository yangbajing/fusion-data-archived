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

package helloscala.data

import java.sql.Timestamp
import java.time._
import java.util.Date

import helloscala.common.types._

import scala.beans.BeanProperty

/**
 * 数据单元
 */
trait Cell {
  // 数据值
  val value: AnyRef

  def getString: Option[String] = AsString.unapply(value)

  def getByte: Option[Byte] = AsByte.unapply(value)

  def getChar: Option[Char] = AsChar.unapply(value)

  def getShort: Option[Short] = AsShort.unapply(value)

  def getInt: Option[Int] = AsInt.unapply(value)

  def getLong: Option[Long] = AsLong.unapply(value)

  def getFloat: Option[Float] = AsFloat.unapply(value)

  def getDouble: Option[Double] = AsDouble.unapply(value)

  def getDate: Option[Date] = AsDate.unapply(value)

  def getTimestamp: Option[Timestamp] = AsTimestamp.unapply(value)

  def getInstant: Option[Instant] = AsInstant.unapply(value)

  def getLocalDate: Option[LocalDate] = AsLocalDate.unapply(value)

  def getLocalTime: Option[LocalTime] = AsLocalTime.unapply(value)

  def getLocalDateTime: Option[LocalDateTime] = AsLocalDateTime.unapply(value)

  def getZonedDateTime: Option[ZonedDateTime] = AsZonedDateTime.unapply(value)
}

trait IndexCell extends Cell {
  val idx: Int
}

case class ElementCell(
    // 数据例索引号，从0开始（Jdbc ResultSet的索引号是从1开始）
    @BeanProperty idx: Int,
    // 数据值
    @BeanProperty value: AnyRef)
    extends IndexCell {}
