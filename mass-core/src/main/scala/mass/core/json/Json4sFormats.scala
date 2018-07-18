package mass.core.json

import com.fasterxml.jackson.databind.ObjectMapper
import helloscala.common.jackson.Jackson
import org.json4s.{DefaultFormats, Serializer}
import org.json4s.jackson.JsonMethods

trait Json4sFormats extends DefaultFormats {
  override val customSerializers: List[Serializer[_]] = JavaTimeSerializers.defaults
}

object Json4sFormats extends Json4sFormats

object Json4sMethods extends JsonMethods {
  override def mapper: ObjectMapper = Jackson.defaultObjectMapper
}
