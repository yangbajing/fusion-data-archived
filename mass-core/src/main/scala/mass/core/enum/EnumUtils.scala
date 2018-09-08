package mass.core.enum

import mass.model.{IdValue, TitleIdValue}

object EnumUtils {

  def enumToIdValues(list: Iterable[Enumeration#Value]): Seq[IdValue] = list.map(v => IdValue(v.id, v.toString)).toSeq

  def enumToTitleIdValues(list: Iterable[Enumeration#Value]): Seq[TitleIdValue] =
    list.map(e => TitleIdValue(e.toString, e.id)).toSeq

  def enumToIdValues(v: Enumeration): Seq[IdValue] = enumToIdValues(v.values)
  def enumToTitleIdValues(v: Enumeration): Seq[TitleIdValue] = enumToTitleIdValues(v.values)

}
