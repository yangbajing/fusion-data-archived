/**
 * multi-jvm:run sample.Sample // {TestName}MultiJvm{NodeName}
 */
package sample

object SampleMultiJvmNode1 {
  def main(args: Array[String]): Unit = {
    println("Hello from node 1")
  }
}

object SampleMultiJvmNode2 {
  def main(args: Array[String]): Unit = {
    println("Hello from node 2")
  }
}

object SampleMultiJvmNode3 {
  def main(args: Array[String]): Unit = {
    println("Hello from node 3")
  }
}
