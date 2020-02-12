package mass.job.component

import helloscala.common.Configuration
import helloscala.common.jackson.Jackson
import helloscala.common.test.HelloscalaSpec
import mass.model.job.{ JobItem, Program }
import mass.job.JobSettings
import mass.server.MassSettings

class JobRunTest extends HelloscalaSpec {
  val configuration = Configuration.load()
  val schedulerConfig = JobSettings(MassSettings(configuration))

  "JobRunTest" should {
    "run java" in {
      val detail = JobItem(Program.JAVA, Seq(), "test.JavaMain")
      val result = JobRun.run(detail, "test-java", schedulerConfig)
      println(Jackson.prettyStringify(result))
      result.exitValue mustBe 0
      result.start must be < result.end.value
    }

    "run scala" in {
      val detail = JobItem(Program.SCALA, Seq(), "test.ScalaMain")
      val result = JobRun.run(detail, "test-scala", schedulerConfig)
      println(Jackson.prettyStringify(result))
      result.exitValue mustBe 0
      result.start must be < result.end.value
    }

    "run bash -c" in {
      val detail = JobItem(Program.SH, Seq("-c"), "echo '哈哈哈'")
      val result = JobRun.run(detail, "test-bash", schedulerConfig)
      println(Jackson.prettyStringify(result))
      result.exitValue mustBe 0
      result.start must be < result.end.value
    }

    "run python -c" in {
      val detail = JobItem(Program.PYTHON, Seq("-c"), "print('哈哈哈')")
      val result = JobRun.run(detail, "test-python", schedulerConfig)
      println(Jackson.prettyStringify(result))
      result.exitValue mustBe 0
      result.start must be < result.end.value
    }
  }
}
