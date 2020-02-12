package mass.extension

import java.nio.file.{ Files, Path, Paths }

import akka.actor.typed.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import fusion.common.extension.{ FusionExtension, FusionExtensionId }
import mass.core.Constants

/**
 * MassCore将作为Akka的库扩展自动加载
 * akka.typed.library-extensions += "mass.extension.MassCore"
 */
final class MassCore private (val system: ActorSystem[_]) extends FusionExtension with StrictLogging {
  private var _tempDirectory: Path = _

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

object MassCore extends FusionExtensionId[MassCore] {
  override def createExtension(system: ActorSystem[_]): MassCore = new MassCore(system)
}
