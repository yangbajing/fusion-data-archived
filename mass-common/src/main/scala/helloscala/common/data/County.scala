/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package helloscala.common.data

import com.typesafe.scalalogging.Logger
import helloscala.common.jackson.Jackson

import scala.util.control.NonFatal

case class County(county: String, postCode: Option[Int] = None)

case class Province(province: String, counties: Seq[County])

case class ProvinceData(provinces: Seq[Province])

object Province {
  private val logger = Logger(getClass.getName.dropRight(1))

  lazy val provinces: ProvinceData = getProvinces()

  def getProvinces(): ProvinceData = {
    val in = Thread.currentThread().getContextClassLoader.getResource("province.json")
    try {
      Jackson.defaultObjectMapper.readValue(in, classOf[ProvinceData])
    } catch {
      case NonFatal(e) =>
        logger.error("获取省份名名称数据错误", e)
        throw e
    }
  }

}
