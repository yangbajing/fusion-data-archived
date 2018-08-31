package example.catsexample

import cats._
import cats.syntax.either._

sealed abstract class DatabaseError
trait DatabaseValue

object Database {
  def databaseThings(): Either[DatabaseError, DatabaseValue] = ???
}

sealed abstract class ServiceError
trait ServiceValue

object Service {
  def serviceThings(v: DatabaseValue): Either[ServiceError, ServiceValue] = ???
}

sealed abstract class AppError

object AppError {
  final case class Database(error: DatabaseError) extends AppError
  final case class Service(error: ServiceError) extends AppError
}

object EitherDemo {

  def doApp(): Either[AppError, ServiceValue] =
    Database
      .databaseThings()
      .leftMap[AppError](AppError.Database)
      .flatMap(dv => Service.serviceThings(dv).leftMap(AppError.Service))

  def awesome: String =
    doApp() match {
      case Left(AppError.Database(_)) => "something in the database went wrong"
      case Left(AppError.Service(_))  => "something in the service went wrong"
      case Right(_)                   => "everything is alright!"
    }

  val either: Either[NumberFormatException, Int] = Either.catchOnly[NumberFormatException]("abc".toInt)

}
