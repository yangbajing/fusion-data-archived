package mass.connector.sql

import mass.core.event.{EventData, Events}

case class EventDataSql(data: JdbcResultSet) extends EventData {
  override def `type`: String = EventDataSql.TYPE
}

object EventDataSql {
  val TYPE = "data/sql"

  Events.registerType(TYPE)
}
