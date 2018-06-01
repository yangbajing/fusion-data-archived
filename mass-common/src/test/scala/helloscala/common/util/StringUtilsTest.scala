package helloscala.common.util

import helloscala.common.test.HelloscalaSpec

class StringUtilsTest extends HelloscalaSpec {

  import StringUtils._

  "StringUtils" should {
    "isBlack" in {
      isBlank("") mustBe true
      isBlank(" ") mustBe true
      isBlank("  ") mustBe true
      isBlank("s") mustBe false
      isBlank(" s ") mustBe false
      isBlank(" s") mustBe false
      isBlank("s ") mustBe false
    }

    "isNoneBlack" in {
      isNoneBlank("s") mustBe true
      isNoneBlank(" s") mustBe true
      isNoneBlank(" s ") mustBe true
      isNoneBlank("s ") mustBe true
    }

    "randomString" in {
      randomString(12).length mustBe 12
    }

    "convertUnderscoreToProperty" in {
      convertUnderscoreToProperty("") mustBe ""
      convertUnderscoreToProperty("a") mustBe "a"
      convertUnderscoreToProperty("a_b") mustBe "aB"
      convertUnderscoreToProperty("a_b_cd") mustBe "aBCd"
      convertUnderscoreToProperty("a_b_cd_efg") mustBe "aBCdEfg"
    }

    "convertPropertyToUnderscore" in {
      convertPropertyToUnderscore("") mustBe ""
      convertPropertyToUnderscore("a") mustBe "a"
      convertPropertyToUnderscore("aB") mustBe "a_b"
      convertPropertyToUnderscore("aBCde") mustBe "a_b_cde"
      convertPropertyToUnderscore("aBCdeFg") mustBe "a_b_cde_fg"
    }
  }

}
