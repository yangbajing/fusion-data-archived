package mass.extension

import java.nio.file.{ Files, Path, Paths }

import akka.actor.typed.ActorSystem
import akka.serialization.jackson.JacksonObjectMapperProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.typesafe.scalalogging.StrictLogging
import fusion.common.extension.{ FusionExtension, FusionExtensionId }
import fusion.core.extension.FusionCore
import mass.MassSettings
import mass.core.Constants

/**
 * MassCore将作为Akka的库扩展自动加载
 * akka.typed.library-extensions += "mass.extension.MassCore"
 */
final class MassCore private (val system: ActorSystem[_]) extends FusionExtension with StrictLogging {
  FusionCore(system)
  val settings: MassSettings = MassSettings(system)
  val jsonMapper: ObjectMapper = JacksonObjectMapperProvider(system).getOrCreate(Constants.JACKSON_JSON, None)
  val cborMapper: ObjectMapper = JacksonObjectMapperProvider(system).getOrCreate(Constants.JACKSON_CBOR, None)
  val tempDirectory: Path = {
    val _tempDirectory = Paths.get(
      configuration.getOrElse[String](s"${Constants.BASE_CONF}.core.temp-dir", System.getProperty("java.io.tmpdir")))
    if (!Files.isDirectory(_tempDirectory)) {
      Files.createDirectories(_tempDirectory)
    }
    _tempDirectory
  }

  logger.info(configuration.getConfig(Constants.BASE_CONF).toString)

  def name: String = configuration.getString(s"${Constants.BASE_CONF}.name")

  override def toString = s"MassCore($system)"
}

object MassCore extends FusionExtensionId[MassCore] {
  override def createExtension(system: ActorSystem[_]): MassCore = new MassCore(system)
}
