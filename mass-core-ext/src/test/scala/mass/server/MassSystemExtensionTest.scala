package mass.server

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import mass.extension.MassSystem
import org.scalatest.wordspec.AnyWordSpecLike

class MassSystemExtensionTest extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  "MassSystemExtension" should {
    "as[MassSystemExtension]" in {
      val massSystem = MassSystem(system)
      massSystem should not be null
      println(massSystem)
    }
  }
}
