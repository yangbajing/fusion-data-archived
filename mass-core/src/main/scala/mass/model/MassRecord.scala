package mass.model

import java.sql.ResultSet

trait MassElement[T] {
  def `type`: String
  def underyling: T
  def get: Option[T]
  def label: String
}

case class MassObjectElement(label: String, underyling: Object, `type`: String = "Object") extends MassElement[Object] {
  override def get: Option[Object] = Option(underyling)
}

/**
 * 数据元素：SQL(ResultSet), CQL(ResultSet), MongoDB(Document), CSV(Line), Excel(Row)
 */
trait MassRecord[RECORD] {
  def underlying: RECORD
}

class SqlResultSetRecord(val underlying: ResultSet) extends MassRecord[ResultSet] {
  private val md = underlying.getMetaData
  private val labels = (1 to md.getColumnCount).map(md.getColumnLabel)


  def getForLabel(label: String): Option[MassElement[_]] =
    Option(underlying.getObject(label)).map(value => MassObjectElement(label, value))
}
