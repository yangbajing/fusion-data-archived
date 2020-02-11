package mass.server

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helloscala.common.test.HelloscalaSpec
import mass.extension.MassSystem
import org.scalatest.BeforeAndAfterAll

class MassSystemExtensionTest extends TestKit(ActorSystem("mass")) with HelloscalaSpec with BeforeAndAfterAll {
  "MassSystemExtension" should {
    "as[MassSystemExtension]" in {
      val massSystem = MassSystem(system)
      massSystem must not be null
      println(massSystem)
    }
  }
}
