package mass.connector.sql

import java.util.concurrent.atomic.AtomicInteger

case class Database(id: Int, name: String)

object Databases {
  val idSeq = new AtomicInteger()
  var databases = Vector.empty[Database]

  def register(name: String): Vector[Database] = {
    databases :+= Database(idSeq.getAndIncrement(), name)
    databases
  }

}
