package mass

import mass.testkit.MassActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class MassSettingsTest extends MassActorTestKit with AnyWordSpecLike {
  "MassSettings" should {
    val settings = MassSettings(mass.system)
    "compiles" in {
      settings.compiles.scala212Home should not be empty
      println(settings.compiles.scala212Home)
    }
  }
}
