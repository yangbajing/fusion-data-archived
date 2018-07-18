package mass.core

import java.io.File
import java.nio.file.{Files, Path, Paths}

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import helloscala.common.Configuration

import scala.reflect.ClassTag

private[mass] abstract class MassSystem(
    val name: String,
    val system: ActorSystem,
    private var _configuration: Configuration
) {

  def configuration: Configuration = _configuration

  def as[T: ClassTag]: T = this.asInstanceOf[T]

  /**
   * @return 返回临时目录
   */
  def tempDir: Path = {
    val tempDirectory = Paths.get(configuration.getOrElse[String]("mass.core.temp-dir", System.getProperty("java.io.tmpdir")))
    if (!Files.isDirectory(tempDirectory)) {
      Files.createDirectories(tempDirectory)
    }
    tempDirectory
  }

  override def toString: String = s"MassSystem($name, $system)"
}

object MassSystem {
  private var _instance: MassSystem = _

  def instance: MassSystem = _instance

  def apply(): MassSystem = {
    val config = ConfigFactory.load()
    val system = ActorSystem(config.getString("mass.name"), config)
    apply(system)
  }

  def apply(system: ActorSystem): MassSystem = apply(system.name, system, Configuration(system.settings.config))

  def apply(name: String, system: ActorSystem): MassSystem = apply(name, system, Configuration(system.settings.config))

  def apply(name: String, system: ActorSystem, configuration: Configuration): MassSystem = {
    val c = Class.forName(configuration.getString("mass.mass-system-class"))
    _instance = c.getDeclaredConstructor(classOf[String], classOf[ActorSystem], classOf[Configuration])
      .newInstance(name, system, configuration)
      .asInstanceOf[MassSystem]
    _instance
  }

}

class MassSystemImpl(
    override val name: String,
    override val system: ActorSystem,
    private var _configuration: Configuration
) extends MassSystem(name, system, _configuration)
