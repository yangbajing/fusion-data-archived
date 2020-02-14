package mass

import fusion.json.jackson.Jackson
import fusion.test.FusionWordSpecLike
import mass.message.job.JobCreateReq
import mass.model.job.Program

case class TestProgram(program: Program, number: Int)

class JacksonTest extends FusionWordSpecLike {
  "Program" should {
    "field" in {
      Jackson.stringify(Program.SCALA) shouldBe """"scala""""
      Jackson.defaultObjectMapper.readValue(""""scala"""", classOf[Program]) shouldBe Program.SCALA
    }

    "object" in {
      val o = TestProgram(Program.JAVA, 2)
      val text = """{"program":"java","number":2}"""
      Jackson.stringify(o) shouldBe text
      Jackson.defaultObjectMapper.readValue(text, classOf[TestProgram]) shouldBe o
    }

    "array" in {
      val arr = Vector(Program.JAVA, Program.SCALA)
      val text = """["java","scala"]"""
      Jackson.stringify(arr) shouldBe text
      Jackson.defaultObjectMapper.readValue[Vector[Program]](text) shouldBe arr
    }
  }

  "mock" should {
    "createJob" in {
      val jsonCreateJob =
        """{"item":{"programVersion":"2.12","key":"ddd","program":"java","programOptions":[],"programMain":"test.Main","programArgs":[]},"trigger":{"triggerType":"simple","key":"ddd","startTime":null,"endTime":null,"interval":"PT24H"}}"""

      val jsonNode = Jackson.readTree(jsonCreateJob)
      println(jsonNode)
      val req = Jackson.treeToValue[JobCreateReq](jsonNode)
      println(req)
      println(Jackson.prettyStringify(req))
    }

    "jackson" in {
      import scala.compat.java8.DurationConverters._
      import scala.concurrent.duration._
      val d = 3.days
      println(Jackson.valueToTree(d))
      println(Jackson.stringify(d))

      val node = Jackson.readTree("69")
      val jd = Jackson.treeToValue[java.time.Duration](node)
      println(jd)
      println(jd.toScala)
    }
  }
}
