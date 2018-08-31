package example.catsexample

import cats.{Applicative, SemigroupK}
import cats.data._
import cats.data.Validated.{Invalid, Valid}
import cats.kernel.Semigroup

trait Read[A] {
  def read(s: String): Option[A]
}

object Read {
  def apply[A](implicit A: Read[A]): Read[A] = A

  implicit val stringRead: Read[String] = (s: String) => Some(s)

  implicit val intRead: Read[Int] = (s: String) =>
    if (s.matches("-?[0-9]+")) Some(s.toInt)
    else None
}

sealed abstract class ConfigError
final case class MissingConfig(field: String) extends ConfigError
final case class ParseError(field: String) extends ConfigError

case class Config(map: Map[String, String]) {

  def parse[A: Read](key: String): Validated[ConfigError, A] =
    map.get(key) match {
      case None => Invalid(MissingConfig(key))
      case Some(value) =>
        Read[A].read(value) match {
          case None    => Invalid(ParseError(key))
          case Some(a) => Valid(a)
        }
    }
}

case class ConnectionParams(url: String, port: Int)

object ParallelValidationDemo extends App {
  implicit val nelSemigroup: Semigroup[NonEmptyList[ConfigError]] = SemigroupK[NonEmptyList].algebra[ConfigError]

//  implicit def validatedApplicative[E: Semigroup]: Applicative[Validated[E, ?]] =
//    new Applicative[Validated[E, ?]] {
//
//      def ap[A, B](f: Validated[E, A => B])(fa: Validated[E, A]): Validated[E, B] =
//        (fa, f) match {
//          case (Valid(a), Valid(fab))     => Valid(fab(a))
//          case (i @ Invalid(_), Valid(_)) => i
//          case (Valid(_), i @ Invalid(_)) => i
//          case (Invalid(e1), Invalid(e2)) => Invalid(Semigroup[E].combine(e1, e2))
//        }
//
//      def pure[A](x: A): Validated[E, A] = Validated.valid(x)
//    }

  def parallelValidate[E: Semigroup, A, B, C](v1: Validated[E, A], v2: Validated[E, B])(
      f: (A, B) => C): Validated[E, C] =
    (v1, v2) match {
      case (Valid(a), Valid(b))       => Valid(f(a, b))
      case (Valid(_), i @ Invalid(_)) => i
      case (i @ Invalid(_), Valid(_)) => i
      case (Invalid(e1), Invalid(e2)) => Invalid(Semigroup.combine(e1, e2))
    }

  val config = Config(Map(("endpoint", "127.0.0.1"), ("port", "not an int")))

  val v1 = parallelValidate(config.parse[String]("url").toValidatedNel, config.parse[Int]("port").toValidatedNel)(
    ConnectionParams.apply)
  // v1: cats.data.Validated[cats.data.NonEmptyList[ConfigError],ConnectionParams] = Invalid(NonEmptyList(MissingConfig(url), ParseError(port)))
  println(s"v1: $v1")

  val v2 = parallelValidate(config.parse[String]("endpoint").toValidatedNel, config.parse[Int]("port").toValidatedNel)(
    ConnectionParams.apply)
  // v2: cats.data.Validated[cats.data.NonEmptyList[ConfigError],ConnectionParams] = Invalid(NonEmptyList(ParseError(port)))
  println(s"v2: $v2")

  val config2 = Config(Map(("endpoint", "127.0.0.1"), ("port", "1234")))
  // config: Config = Config(Map(endpoint -> 127.0.0.1, port -> 1234))

  val v3 = parallelValidate(config2.parse[String]("endpoint").toValidatedNel,
                            config2.parse[Int]("port").toValidatedNel)(ConnectionParams.apply)
  // v3: cats.data.Validated[cats.data.NonEmptyList[ConfigError],ConnectionParams] = Valid(ConnectionParams(127.0.0.1,1234))
  println(s"v3: $v3")
}
