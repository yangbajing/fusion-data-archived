package kafkasample.demo

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util

import helloscala.common.util.StringUtils
import org.apache.kafka.common.serialization.Serializer

case class Customer(customerId: Int, customerName: String) {

}

class CustomerSerializer extends Serializer[Customer] {
  private val EMPTY_NAME = Array[Byte]()

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {}

  override def serialize(topic: String, data: Customer): Array[Byte] = {
    if (data eq null) {
      null
    } else {
      var nameLen = 0
      var nameBytes = EMPTY_NAME
      if (StringUtils.isNoneBlank(data.customerName)) {
        nameLen = data.customerName.length
        nameBytes = data.customerName.getBytes(StandardCharsets.UTF_8)
      }

      val buf = ByteBuffer.allocate(4 + 4 + nameLen)
      buf.putInt(data.customerId)
      buf.putInt(nameLen)
      buf.put(nameBytes)

      buf.array()
    }
  }

  override def close(): Unit = ???
}
