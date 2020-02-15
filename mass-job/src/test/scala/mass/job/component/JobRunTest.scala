package mass.job.component

import fusion.json.jackson.Jackson
import mass.MassSettings
import mass.job.JobSettings
import mass.model.job.{ JobItem, Program }
import mass.testkit.MassActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class JobRunTest extends MassActorTestKit with AnyWordSpecLike {
  private val jobSettings = JobSettings(MassSettings(system))

  "JobRunTest" should {
    "run java" in {
      val item = JobItem(Program.JAVA, Seq(), "test.JavaMain")
      val result = JobRun.run(item, "test-java", jobSettings)
      println(Jackson.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end
    }

    "run scala" in {
      val item = JobItem(Program.SCALA, Seq(), "test.ScalaMain")
      val result = JobRun.run(item, "test-scala", jobSettings)
      println(Jackson.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end
    }

    "run bash -c" in {
      val item = JobItem(Program.SH, Seq("-c"), "echo '哈哈哈'")
      val result = JobRun.run(item, "test-bash", jobSettings)
      println(Jackson.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end
    }

    "run python -c" in {
      val item = JobItem(Program.PYTHON, Seq("-c"), "print('哈哈哈')")
      val result = JobRun.run(item, "test-python", jobSettings)
      println(Jackson.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end
    }
  }
}
