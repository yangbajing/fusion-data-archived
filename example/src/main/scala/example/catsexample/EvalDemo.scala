package example.catsexample
import cats.Eval

object MutualRecursion {

  def even(n: Int): Eval[Boolean] =
    Eval.always(n == 0).flatMap {
      case true  => Eval.now(true)
      case false => odd(n - 1)
    }

  def odd(n: Int): Eval[Boolean] =
    Eval.always(n == 0).flatMap {
      case true  => Eval.now(false)
      case false => even(n - 1)
    }
}
// defined object MutualRecursion

object EvalDemo extends App {
  val result = MutualRecursion.odd(199999).value
  println(result)
}
