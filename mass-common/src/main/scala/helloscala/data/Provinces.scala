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
import com.typesafe.scalalogging.Logger
import helloscala.common.jackson.Jackson

import scala.util.control.NonFatal

object Provinces {
  private val logger = Logger(getClass.getName.dropRight(1))

  lazy val provinces: ProvinceData = getProvinces()

//
  def getProvinces(): ProvinceData = {
    val in =
      Thread.currentThread().getContextClassLoader.getResource("province.json")
    try {
      Jackson.defaultObjectMapper.readValue(in, classOf[ProvinceData])
    } catch {
      case NonFatal(e) =>
        logger.error("获取省份名名称数据错误", e)
        throw e
    }
  }

}
