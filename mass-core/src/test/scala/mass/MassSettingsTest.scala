package mass

import helloscala.common.Configuration
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MassSettingsTest extends AnyWordSpec with Matchers {
  "MassSettings" should {
    val config = Configuration.generateConfig()
    val settings = MassSettings(config)

    "compiles" in {
      settings.compiles.scala212Home should not be empty
      println(settings.compiles.scala212Home)
    }

    "test key" in {
      config.getString("test.key") shouldBe "test.key"
    }
  }
}
