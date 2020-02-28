package mass.job.util

import java.nio.charset.StandardCharsets
import java.nio.file.{ Files, Paths }

import fusion.inject.guice.testkit.GuiceApplicationTestkit
import fusion.json.jackson.ScalaObjectMapper
import mass.MassSettings
import mass.job.JobSettings
import mass.message.job.JobUploadJobReq
import org.scalatest.wordspec.AnyWordSpecLike

class JobUtilsTest extends GuiceApplicationTestkit with AnyWordSpecLike {
  private implicit val ec = typedSystem.executionContext
  private val objectMapper = injectInstance[ScalaObjectMapper]
  private val jobSettings = JobSettings(MassSettings(configuration))

  "JobService" should {
    "uploadJob" in {
      val fileName = "hello.zip"
      val originalFile =
        Paths.get(sys.props.get("user.dir").get + "/mass-job/src/universal/examples/sample-job/" + fileName)
      val file2 = originalFile.getParent.resolve("hello2.zip")
      Files.copy(originalFile, file2)
      println("file json string: " + objectMapper.stringify(file2))
      val result =
        JobUtils.uploadJob(jobSettings, JobUploadJobReq(file2, fileName, StandardCharsets.UTF_8)).futureValue
      println(result)
    }
  }

  "conf" should {
    val str = """#key=sample
                |item {
                |  name = "Hello world!"
                |  program = "java" # Job程序类型，当前支持Java，sh（bash），python
                |  program-main = "hello.Hello" # 可执行Java主类名字，[需要将程序打成Jar包。job-program为java时有效
                |}
                |trigger {
                |  trigger-type = "cron" # Job类型，当前支持：simple、cron、event三种
                |  start-time = "2020-03-03 10:10:10" # Job开始执行时间（可选）
                |  end-time = "2020-03-13 10:10:10" # Job结束时间（可选）
                |  repeat = 4 # Job重复次数，job-type为simple时有效
                |  duration = 120.seconds # 两次Job之间的时间间隔，job-type为simple时有效
                |  cron-express = "1 0 0 * * ?" # 基于CRON的日历调度配置，job-type为cron时有效
                |}""".stripMargin
    "parse" in {
      val either = JobUtils.parseJobConf(str)
      println(either)
      val req = either.toOption.value
      req.item.name shouldBe Some("Hello world!")
    }
  }
}
