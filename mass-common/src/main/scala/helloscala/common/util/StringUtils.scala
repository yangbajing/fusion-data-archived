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

package helloscala.common.util

import java.io._
import java.security.SecureRandom
import java.util.{Map => JMap, HashMap => JHashMap}

import scala.annotation.tailrec
import scala.collection.immutable
import scala.compat.java8.FunctionConverters.asJavaBiConsumer

object StringUtils {

  val BLACK_CHAR: Char = ' '
  val PRINTER_CHARS: immutable.IndexedSeq[Char] = ('0' to '9') ++ ('a' to 'z') ++ ('A' to 'Z')
  private val HEX_CHARS: Array[Char] = "0123456789abcdef".toCharArray
  private val HEX_CHAR_SETS = Set[Char]() ++ ('0' to '9') ++ ('a' to 'f') ++ ('A' to 'F')

  def extractFirstName(msg: Any): Option[String] = msg match {
    case c: AnyRef =>
      val s = convertPropertyToUnderscore(c.getClass.getSimpleName)
      Some(s.take(s.indexOf('_')))
    case _ => None
  }

  def option(text: String): Option[String] = if (isBlank(text)) None else Some(text)

  def option(text: Option[String]): Option[String] = text.flatMap(option)

  @inline def isHex(c: Char): Boolean = HEX_CHAR_SETS.contains(c)

  /** Turns an array of Byte into a String representation in hexadecimal. */
  def hex2Str(bytes: Array[Byte]): String = {
    val hex = new Array[Char](2 * bytes.length)
    var i = 0
    while (i < bytes.length) {
      hex(2 * i) = HEX_CHARS((bytes(i) & 0xF0) >>> 4)
      hex(2 * i + 1) = HEX_CHARS(bytes(i) & 0x0F)
      i = i + 1
    }
    new String(hex)
  }

  /** Turns a hexadecimal String into an array of Byte. */
  def str2Hex(str: String): Array[Byte] = {
    val bytes = new Array[Byte](str.length / 2)
    var i = 0
    while (i < bytes.length) {
      bytes(i) = Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16).toByte
      i += 1
    }
    bytes
  }

  def isEmpty(s: CharSequence): Boolean =
    (s eq null) || s.length() == 0

  @inline
  def isNoneEmpty(s: CharSequence): Boolean = !isEmpty(s)

  def isBlank(s: CharSequence): Boolean = {
    @tailrec
    def isNoneBlankChar(s: CharSequence, i: Int): Boolean =
      if (i < s.length()) s.charAt(i) != BLACK_CHAR || isNoneBlankChar(s, i + 1)
      else false

    isEmpty(s) || !isNoneBlankChar(s, 0)
  }

  @inline
  def isNoneBlank(s: CharSequence): Boolean = !isBlank(s)

  def randomString(size: Int): String = {
    new SecureRandom()
    val random = SecureRandom.getInstanceStrong
    val len = PRINTER_CHARS.length
    (0 until size).map(_ => PRINTER_CHARS(random.nextInt(len))).mkString
  }

  /**
   * 字符串从属性形式转换为下划线形式
   *
   * @param name    待转字符串
   * @param isLower 转换成下划线形式后是否使用小写，false将完全使用大写
   * @return 转换后字符串
   */
  def convertPropertyToUnderscore(name: String, isLower: Boolean = true): String =
    if (isBlank(name)) {
      name
    } else {
      val sb = new StringBuilder
      for (c <- name) {
        if (Character.isUpperCase(c)) {
          sb.append('_')
        }
        sb.append(
          if (isLower) Character.toLowerCase(c)
          else Character.toUpperCase(c.toUpper)
        )
      }
      sb.toString()
    }

  /**
   * Convert a column name with underscores to the corresponding property name using "camel case".  A name
   * like "customer_number" would match a "customerNumber" property name.
   *
   * @param name the column name to be converted
   * @return the name using "camel case"
   */
  def convertUnderscoreToProperty(name: String): String =
    if (isBlank(name)) {
      ""
    } else {
      val arr = name.split('_')
      arr.head + arr.tail.map(item => item.head.toUpper + item.tail).mkString
    }

  def convertUnderscoreNameToPropertyName(obj: Map[String, Any]): Map[String, Any] =
    obj.map { case (key, value) => convertUnderscoreNameToPropertyName(key) -> value }

  def convertUnderscoreNameToPropertyName(obj: JMap[String, Object]): JMap[String, Object] = {
    val result = new JHashMap[String, Object]()
    val func: (String, Object) => Unit = (key, value) => result.put(convertUnderscoreNameToPropertyName(key), value)
    obj.forEach(asJavaBiConsumer(func))
    result
  }

  def convertUnderscoreNameToPropertyName(name: String): String = {
    val result = new StringBuilder
    var nextIsUpper = false
    if (name != null && name.length > 0) {
      if (name.length > 1 && name.substring(1, 2) == "_") {
        result.append(name.substring(0, 1).toUpperCase)
      } else {
        result.append(name.substring(0, 1).toLowerCase)
      }

      var i = 1
      val len = name.length
      while (i < len) {
        val s = name.substring(i, i + 1)
        if (s == "_") {
          nextIsUpper = true
        } else if (nextIsUpper) {
          result.append(s.toUpperCase)
          nextIsUpper = false
        } else {
          result.append(s.toLowerCase)
        }

        i += 1
      }
    }
    result.toString
  }

  /**
   * Check that the given {@code CharSequence} is neither {@code null} nor
   * of length 0.
   * <p>Note: this method returns {@code true} for a {@code CharSequence}
   * that purely consists of whitespace.
   * <p><pre class="code">
   * StringUtils.hasLength(null) = false
   * StringUtils.hasLength("") = false
   * StringUtils.hasLength(" ") = true
   * StringUtils.hasLength("Hello") = true
   * </pre>
   *
   * @param str the {@code CharSequence} to check (may be {@code null})
   * @return {@code true} if the {@code CharSequence} is not {@code null} and has length
   * @see #hasText(String)
   */
  def hasLength(str: CharSequence): Boolean = { str != null && str.length > 0 }

  /**
   * Check that the given {@code String} is neither {@code null} nor of length 0.
   * <p>Note: this method returns {@code true} for a {@code String} that
   * purely consists of whitespace.
   *
   * @param str the {@code String} to check (may be {@code null})
   * @return {@code true} if the {@code String} is not {@code null} and has length
   * @see #hasLength(CharSequence)
   * @see #hasText(String)
   */
  def hasLength(str: String): Boolean = hasLength(str.asInstanceOf[CharSequence])

  /**
   * Trim <i>all</i> whitespace from the given {@code String}:
   * leading, trailing, and in between characters.
   *
   * @param str the {@code String} to check
   * @return the trimmed {@code String}
   * @see Character#isWhitespace
   */
  def trimAllWhitespace(str: String): String = {
    if (!hasLength(str)) { return str }
    val len = str.length
    val sb = new StringBuilder(str.length)
    var i = 0
    while ({ i < len }) {
      val c = str.charAt(i)
      if (!Character.isWhitespace(c)) { sb.append(c) }

      { i += 1; i - 1 }
    }
    sb.toString
  }

  def toString(e: Throwable): String = {
    val pw = new PrintWriter(new StringWriter())
    try {
      e.printStackTrace(pw)
      pw.toString
    } finally {
      Utils.closeQuiet(pw)
    }
  }
}
