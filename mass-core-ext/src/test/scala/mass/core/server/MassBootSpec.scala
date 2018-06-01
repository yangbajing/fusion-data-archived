package mass.core.server

import helloscala.common.test.HelloscalaSpec
import org.scalatest.BeforeAndAfterAll

trait MassBootSpec extends HelloscalaSpec with BeforeAndAfterAll {

  override protected def afterAll(): Unit = {
    MassBoot.stop()
    super.afterAll()
  }

}
