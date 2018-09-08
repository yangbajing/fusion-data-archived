package mass.job.util

import java.io.File
import java.nio.charset.StandardCharsets

import com.typesafe.config.ConfigFactory
import helloscala.common.Configuration
import helloscala.common.test.HelloscalaSpec
import mass.job.JobSettings
import mass.job.model.JobUploadJobReq
import mass.server.MassSettings

class JobUtilsTest extends HelloscalaSpec {

  import scala.concurrent.ExecutionContext.Implicits.global

  val configuration = Configuration()
  val schedulerConf = JobSettings(MassSettings(configuration))

  "JobService" should {
    "uploadJob" in {
      val fileName = "job.zip"
      val file =
        new File(sys.props.get("user.dir").get + "/mass-scheduler/src/universal/examples/sample-job/" + fileName)
      val result =
        JobUtils
          .uploadJob(schedulerConf, JobUploadJobReq(file, fileName, StandardCharsets.UTF_8))
          .futureValue
      println(result)
    }
  }

  "conf" should {
    val str = """
                |  name = "Job名字"
                |  program = "java" # Job程序类型，当前支持Java，sh（bash），python
                |  program-main = "example.Main" # 可执行Java主类名字，[需要将程序打成Jar包。job-program为java时有效
                |  program-version = "python2.7" # Python程序。可选，默认使用python2.7。job-program为python时有效
                |  trigger-type = "cron" # Job类型，当前支持：simple、cron、event三种
                |  start-time = "yyyy-MM-dd HH:mm:ss" # Job开始执行时间（可选）
                |  end-time = "yyyy-MM-dd HH:mm:ss" # Job结束时间（可选）
                |  repeat = 4 # Job重复次数，job-type为simple时有效
                |  duration = 120.seconds # 两次Job之间的时间间隔，job-type为simple时有效
                |  cron-express = "1 0 0 * * ?" # 基于CRON的日历调度配置，job-type为cron时有效
                |""".stripMargin
    "parse" in {
      val conf = Configuration(ConfigFactory.parseString(str))
      println(conf)
      conf.getString("name") mustBe "Job名字"
    }
  }
}
