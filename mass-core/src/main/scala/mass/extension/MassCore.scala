package mass.extension

import java.nio.file.{ Files, Path, Paths }

import akka.actor.ExtendedActorSystem
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
final class MassCore private (override val classicSystem: ExtendedActorSystem)
    extends FusionExtension
    with StrictLogging {
  FusionCore(classicSystem)
  val settings: MassSettings = MassSettings(classicSystem.settings.config)
  val jsonMapper: ObjectMapper = JacksonObjectMapperProvider(classicSystem).getOrCreate(Constants.JACKSON_JSON, None)
  val cborMapper: ObjectMapper = JacksonObjectMapperProvider(classicSystem).getOrCreate(Constants.JACKSON_CBOR, None)
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

  override def toString = s"MassCore($classicSystem)"
}

object MassCore extends FusionExtensionId[MassCore] {
  override def createExtension(system: ExtendedActorSystem): MassCore = new MassCore(system)
}
