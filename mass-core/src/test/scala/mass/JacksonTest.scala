package mass

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import fusion.json.jackson.JacksonObjectMapperExtension
import fusion.testkit.FusionWordSpecLike
import mass.message.job.JobCreateReq
import mass.model.job.Program

case class TestProgram(program: Program, number: Int)

class JacksonTest extends ScalaTestWithActorTestKit with FusionWordSpecLike {
  private val objectMapper = JacksonObjectMapperExtension(system).objectMapperJson
  "Program" should {
    "field" in {
      objectMapper.stringify(Program.SCALA) shouldBe """"scala""""
      objectMapper.readValue(""""scala"""", classOf[Program]) shouldBe Program.SCALA
    }

    "object" in {
      val o = TestProgram(Program.JAVA, 2)
      val text = """{"program":"java","number":2}"""
      objectMapper.stringify(o) shouldBe text
      objectMapper.readValue(text, classOf[TestProgram]) shouldBe o
    }

    "array" in {
      val arr = Vector(Program.JAVA, Program.SCALA)
      val text = """["java","scala"]"""
      objectMapper.stringify(arr) shouldBe text
      objectMapper.readValue[Vector[Program]](text) shouldBe arr
    }
  }

  "mock" should {
    "createJob" in {
      val jsonCreateJob =
        """{"item":{"programVersion":"2.12","key":"ddd","program":"java","programOptions":[],"programMain":"test.Main","programArgs":[]},"trigger":{"triggerType":"simple","key":"ddd","startTime":null,"endTime":null,"interval":"PT24H"}}"""

      val jsonNode = objectMapper.readTree(jsonCreateJob)
      println(jsonNode)
      val req = objectMapper.treeToValue[JobCreateReq](jsonNode)
      println(req)
      println(objectMapper.prettyStringify(req))
    }

    "jackson" in {
      import scala.compat.java8.DurationConverters._
      import scala.concurrent.duration._
      val d = 3.days
      println(objectMapper.valueToTree(d))
      println(objectMapper.stringify(d))

      val node = objectMapper.readTree("69")
      val jd = objectMapper.treeToValue[java.time.Duration](node)
      println(jd)
      println(jd.toScala)
    }
  }
}
