package mass.job.component

import fusion.inject.guice.testkit.GuiceApplicationTestkit
import fusion.json.jackson.ScalaObjectMapper
import mass.MassSettings
import mass.job.JobSettings
import mass.model.job.{ JobItem, Program }
import org.scalatest.wordspec.AnyWordSpecLike

class JobRunTest extends GuiceApplicationTestkit with AnyWordSpecLike {
  private val jobSettings = JobSettings(MassSettings(config))
  private val objectMapper = injectInstance[ScalaObjectMapper]

  "JobRunTest" should {
    "run java" in {
      val item = JobItem(Program.JAVA, Seq(), "test.JavaMain")
      val result = JobRun.run(item, "test-java", jobSettings)
      println(objectMapper.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end
    }

    "run scala" in {
      val item = JobItem(Program.SCALA, Seq(), "test.ScalaMain")
      val result = JobRun.run(item, "test-scala", jobSettings)
      println(objectMapper.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end
    }

    "run bash -c" in {
      val item = JobItem(Program.SH, Seq("-c"), "echo '哈哈哈'")
      val result = JobRun.run(item, "test-bash", jobSettings)
      println(objectMapper.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end
    }

    "run python -c" in {
      val item = JobItem(Program.PYTHON, Seq("-c"), "print('哈哈哈')")
      val result = JobRun.run(item, "test-python", jobSettings)
      println(objectMapper.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end
    }
  }
}
