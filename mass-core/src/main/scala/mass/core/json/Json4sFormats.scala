//package mass.core.json
//
//import com.fasterxml.jackson.databind.ObjectMapper
//import helloscala.common.jackson.Jackson
//import mass.model.CommonStatus
//import org.json4s.JsonAST.JInt
//import org.json4s.jackson.JsonMethods
//import org.json4s.{CustomSerializer, DefaultFormats, Serializer}
//
//trait Json4sFormats extends DefaultFormats {
//  override val customSerializers: List[Serializer[_]] =
//    new CustomSerializer[CommonStatus](_ =>
//      ({
//        case JInt(i) => CommonStatus.fromValue(i.intValue())
//      }, {
//        case s: CommonStatus => JInt(s.value)
//      })) ::
//      JavaTimeSerializers.defaults
//}
//
//object Json4sFormats extends Json4sFormats
//
//object Json4sMethods extends JsonMethods {
//  override def mapper: ObjectMapper = Jackson.defaultObjectMapper
//}
