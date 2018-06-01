package sample.multinode

import akka.remote.testkit.{MultiNodeSpec, MultiNodeSpecCallbacks}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.language.implicitConversions

trait STMultiNodeSpec
  extends MultiNodeSpecCallbacks
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {
  self: MultiNodeSpec =>

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    multiNodeSpecBeforeAll()
  }

  override protected def afterAll(): Unit = {
    multiNodeSpecAfterAll()
    super.afterAll()
  }

  // Might not be needed anymore if we find a nice way to tag all logging from a node
  override implicit def convertToWordSpecStringWrapper(s: String): WordSpecStringWrapper = new WordSpecStringWrapper(s"$s (on node '${self.myself.name}', $getClass)")
}
