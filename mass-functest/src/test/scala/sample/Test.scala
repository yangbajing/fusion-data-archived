package sample

object Test {
  def filter(source: Seq[Int]): Unit = {
    val result = Array.ofDim[Int](source.size)
    var i = 0
    source.foreach { x =>
      result(i) = if (x % 2 != 0) x * 2 else x
      i += 1
    }
  }

  def record(fn: () => Unit): Unit = {
    val startTime = System.currentTimeMillis()
    fn()
    val endTime = System.currentTimeMillis()
    println(s"运算开销：${endTime - startTime} ms")
  }

  def main(args: Array[String]): Unit = {
    val NUMBER = 100000000
    val array2 = 1 to NUMBER
    record(() => filter(array2))
    record(() => filter(array2))
  }
}
