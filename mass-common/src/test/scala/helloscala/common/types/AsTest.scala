package helloscala.common.types

import helloscala.common.test.HelloscalaSpec

class AsTest extends HelloscalaSpec {

  "As" should {
    "AsInt" in {
      val intNull: Integer = null
      val intValue: Integer = 3
      AsInt.unapply(3) mustBe Some(3)
      AsInt.unapply(3) mustBe Some(Int.box(3))
      AsInt.unapply(intValue) mustBe Some(3)
      AsInt.unapply(null) mustBe None
      AsInt.unapply(intNull) mustBe None
      AsInt.unapply(3.3) mustBe None
      AsInt.unapply(9898L) mustBe Some(9898)
    }
  }

}
