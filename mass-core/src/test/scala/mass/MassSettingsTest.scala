package mass

import mass.core.MassActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class MassSettingsTest extends MassActorTestKit with AnyWordSpecLike {
  "MassSettings" should {
    val settings = MassSettings(system.settings.config)
    "compiles" in {
      settings.compiles.scala212Home should not be empty
      println(settings.compiles.scala212Home)
    }
  }
}
