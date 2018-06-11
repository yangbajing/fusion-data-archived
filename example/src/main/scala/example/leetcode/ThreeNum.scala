package example.leetcode

object ThreeNum {
  def threeSum(nums: Array[Int]): List[List[Int]] = {
    var results = List.empty[List[Int]]

    val len = nums.length
    var i = 0
    while (i < len - 2) {
      var j = i + 1
      while (j < len - 1) {
        var k = i + 2
        while (k < len) {
          val x = nums(i)
          val y = nums(j)
          val z = nums(k)
          println(s"$x, $y, $z")
          if ((x + y + z) == 0) {
            results ::= List(x, y, z)
          }
          k += 1
        }
        j += 1
      }
      i += 1
    }

    results
  }

  def main(args: Array[String]): Unit = {
    val nums = Array(-3, -2, -1, 0, 1, 2, 3)
    val results = threeSum(nums)
    results.foreach(println)
  }
}
