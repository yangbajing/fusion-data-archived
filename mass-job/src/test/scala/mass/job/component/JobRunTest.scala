package mass.job.component

import fusion.json.jackson.Jackson
import helloscala.common.Configuration
import mass.MassSettings
import mass.core.MassActorTestKit
import mass.job.JobSettings
import mass.model.job.{ JobItem, Program }
import org.scalatest.wordspec.AnyWordSpecLike

class JobRunTest extends MassActorTestKit with AnyWordSpecLike {
  val configuration = Configuration.load()
  val schedulerConfig = JobSettings(MassSettings(configuration))

  "JobRunTest" should {
    "run java" in {
      val detail = JobItem(Program.JAVA, Seq(), "test.JavaMain")
      val result = JobRun.run(detail, "test-java", schedulerConfig)
      println(Jackson.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end.value
    }

    "run scala" in {
      val detail = JobItem(Program.SCALA, Seq(), "test.ScalaMain")
      val result = JobRun.run(detail, "test-scala", schedulerConfig)
      println(Jackson.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end.value
    }

    "run bash -c" in {
      val detail = JobItem(Program.SH, Seq("-c"), "echo '哈哈哈'")
      val result = JobRun.run(detail, "test-bash", schedulerConfig)
      println(Jackson.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end.value
    }

    "run python -c" in {
      val detail = JobItem(Program.PYTHON, Seq("-c"), "print('哈哈哈')")
      val result = JobRun.run(detail, "test-python", schedulerConfig)
      println(Jackson.prettyStringify(result))
      result.exitValue shouldBe 0
      result.start should be < result.end.value
    }
  }
}
