package mass.server

import akka.actor.ActorSystem
import akka.testkit.TestKit
import helloscala.common.test.HelloscalaSpec
import mass.core.MassSystem
import org.scalatest.BeforeAndAfterAll

class MassSystemExtensionTest extends TestKit(ActorSystem("mass")) with HelloscalaSpec with BeforeAndAfterAll {

  override protected def beforeAll(): Unit = {
    MassSystem(system)
  }

  "MassSystemExtension" should {
    "as[MassSystemExtension]" in {
      val massSystem = MassSystem.instance
      massSystem must not be null

      val mse = massSystem.as[MassSystemExtension]
      println(mse)
    }
  }

}
