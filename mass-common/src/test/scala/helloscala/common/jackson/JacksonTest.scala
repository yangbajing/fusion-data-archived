package helloscala.common.jackson

import helloscala.common.test.HelloscalaSpec
import helloscala.data.{IdValue, NameValue}

class JacksonTest extends HelloscalaSpec {

  "Jackson" should {
    "NameValue" in {
      val nv = NameValue("name", "value")
      println(nv)
      println(Jackson.stringify(nv))
    }
    "IdValue" in {
      val nv = IdValue(2, "value")
      println(nv)
      println(Jackson.stringify(nv))
    }
  }

}
