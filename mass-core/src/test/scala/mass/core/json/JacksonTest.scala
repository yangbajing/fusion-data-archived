package mass.core.json

import helloscala.common.jackson.Jackson
import helloscala.common.test.HelloscalaSpec
import mass.data.{ IdValue, NameValue }

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
