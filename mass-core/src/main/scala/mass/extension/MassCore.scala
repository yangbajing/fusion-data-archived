package mass.extension

import java.nio.file.{Files, Path, Paths}

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.typesafe.scalalogging.StrictLogging
import helloscala.common.Configuration
import mass.core.Constants

/**
 * MassCore将作为Akka的库扩展自动加载
 * library-extensions += "mass.extension.MassCore"
 */
final class MassCore private (val system: ExtendedActorSystem) extends Extension with StrictLogging {
  private var _tempDirectory: Path = _

  val configuration = Configuration(system.settings.config)

  logger.info(configuration.getConfig(Constants.BASE_CONF).toString)

  def name: String = configuration.getString(s"${Constants.BASE_CONF}.name")

  def tempDirectory: Path = synchronized {
    _tempDirectory = Paths.get(
      configuration.getOrElse[String](s"${Constants.BASE_CONF}.core.temp-dir", System.getProperty("java.io.tmpdir")))
    if (!Files.isDirectory(_tempDirectory)) {
      Files.createDirectories(_tempDirectory)
    }
    _tempDirectory
  }

  override def toString = s"MassCore($system)"
}

object MassCore extends ExtensionId[MassCore] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): MassCore = new MassCore(system)
  override def lookup(): ExtensionId[_ <: Extension] = MassCore
}
