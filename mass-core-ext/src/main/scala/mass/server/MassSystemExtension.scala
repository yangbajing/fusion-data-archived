package mass.server

import akka.actor.ActorSystem
import helloscala.common.Configuration
import javax.sql.DataSource
import mass.core.MassSystem
import mass.core.jdbc.JdbcTemplate
import mass.slick.SlickProfile
import slick.jdbc.DataSourceJdbcDataSource

class MassSystemExtension(
    override val name: String,
    override val system: ActorSystem,
    private var _configuration: Configuration
) extends MassSystem(name, system, _configuration) {

  private val postgresProps = configuration.getConfiguration("mass.core.persistence.postgres")
  val slickDatabase: SlickProfile.backend.DatabaseDef = SlickProfile.createDatabase(postgresProps)
  val dataSource: DataSource = slickDatabase.source.asInstanceOf[DataSourceJdbcDataSource].ds
  val jdbcTemplate = JdbcTemplate(dataSource, postgresProps)
  init()

  private def init(): Unit = {
    system.registerOnTermination {
      slickDatabase.close()
    }
  }

  override def toString: String = s"MassSystemExtension($name, $system, $configuration, $dataSource)"
}

object MassSystemExtension {
  def instance: MassSystemExtension = MassSystem.instance.as[MassSystemExtension]
}
