import helloscala.common.test.HelloscalaSpec

class Test extends HelloscalaSpec {

  "dd" in {
    val job1 = 'job
    val job2 = 'job
    println(job1 == job2)
    println(job1 eq job2)
  }

  "str" in {
    val job1 = "job"
    val job2 = "job"
    println(job1 == job2)
    println(job1 eq job2)
  }

  "new str" in {
    val job1 = new String("job")
    val job2 = new String("job")
    println(job1 == job2)
    println(job1 eq job2)
  }

}
