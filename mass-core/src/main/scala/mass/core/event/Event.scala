package mass.core.event

trait Event {
  def `type`: String
}

trait EventData extends Event {
  def data: AnyRef
}

case class EventDataSimple(data: AnyRef) extends EventData {
  override def `type`: String = "data/simple"
}

object Events {
  private var _types = Vector.empty[String]

  def registerType(`type`: String): Vector[String] = {
    if (!_types.contains(`type`)) {
      _types = _types :+ `type`
    }
    _types
  }

  def listTypes: Vector[String] = _types
}
