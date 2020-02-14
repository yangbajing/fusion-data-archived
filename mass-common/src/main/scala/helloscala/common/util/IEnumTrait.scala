package helloscala.common.util

import helloscala.common.data.ValueName

trait IEnumTrait[V] {
  self =>
  val companion: IEnumTraitCompanion[V]
  val value: V
  val name: String = StringUtils.dropLast$(this.getClass.getSimpleName)
  val toValueName: ValueName[V] = new ValueName[V] {
    override def value: V = self.value
    override def name: String = self.name
  }
}
trait IEnumTraitCompanion[V] {
  type Value <: IEnumTrait[V]
  val values: Vector[Value]
  final def optionFromValue(value: String): Option[Value] = values.find(_.value == value)
  final def fromValue(value: String): Value =
    optionFromValue(value).getOrElse(
      throw new NoSuchElementException(s"${getClass.getSimpleName}.values by value not found, it is $value."))
  final def optionFromName(name: String): Option[Value] = values.find(_.name == name)
  final def fromName(name: String): Value =
    optionFromName(name).getOrElse(
      throw new NoSuchElementException(s"${getClass.getSimpleName}.values by name not found, it is $name."))
}
