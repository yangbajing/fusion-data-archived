//package mass.core.json
//
//import org.json4s.JsonAST.{JInt, JString}
//import org.json4s.reflect.TypeInfo
//import org.json4s.{Formats, JValue, MappingException, Serializer}
//
//import scala.reflect.ClassTag
//
//class EnumSerializer[E <: Enumeration: ClassTag](enum: E) extends Serializer[E#Value] {
//  import org.json4s.JsonDSL._
//
//  val EnumerationClass = classOf[E#Value]
//
//  private[this] def isValid(json: JValue): Boolean = json match {
//    case JInt(value) => value <= enum.maxId
//    case _           => false
//  }
//
//  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), E#Value] = {
//    case (TypeInfo(EnumerationClass, _), json) if isValid(json) =>
//      json match {
//        case JInt(value) => enum(value.toInt)
//        case value =>
//          throw new MappingException(s"Can't convert $value to $EnumerationClass")
//      }
//  }
//
//  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
//    case i: E#Value => i.id
//  }
//}
//
//class EnumNameSerializer[E <: Enumeration: ClassTag](enum: E) extends Serializer[E#Value] {
//  import org.json4s.JsonDSL._
//
//  val EnumerationClass = classOf[E#Value]
//
//  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), E#Value] = {
//    case (t @ TypeInfo(EnumerationClass, _), json) if isValid(json) => {
//      json match {
//        case JString(value) => enum.withName(value)
//        case value =>
//          throw new MappingException(s"Can't convert $value to $EnumerationClass")
//      }
//    }
//  }
//
//  private[this] def isValid(json: JValue): Boolean = json match {
//    case JString(value) if enum.values.exists(_.toString == value) => true
//    case _                                                         => false
//  }
//
//  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
//    case i: E#Value => i.toString
//  }
//}
