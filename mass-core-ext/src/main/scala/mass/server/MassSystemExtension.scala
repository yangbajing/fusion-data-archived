//package mass.server
//
//import akka.actor.ActorSystem
//import helloscala.common.Configuration
//import mass.core.MassSystem
//import mass.slick.SqlManager
//
//class MassSystemExtension(
//    override val name: String,
//    override val system: ActorSystem,
//    private var _configuration: Configuration
//) extends MassSystem(name, system, _configuration) {
//
//  val sqlManager = SqlManager(configuration)
//
//  override def init(): Unit =
//    sys.addShutdownHook {
//      sqlManager.slickDatabase.close()
//    }
//
//  override def toString: String =
//    s"MassSystemExtension($name, $system, $configuration, $sqlManager)"
//}
//
//object MassSystemExtension {
//  def instance: MassSystemExtension = MassSystem.instance.as[MassSystemExtension]
//}
