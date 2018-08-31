package example.catsexample

final case class RegistrationData(username: String, password: String, firstName: String, lastName: String, age: Int)
sealed trait DomainValidation {
  def errorMessage: String
}

case object UsernameHasSpecialCharacters extends DomainValidation {
  def errorMessage: String = "Username cannot contain special characters."
}

case object PasswordDoesNotMeetCriteria extends DomainValidation {

  def errorMessage: String =
    "Password must be at least 10 characters long, including an uppercase and a lowercase letter, one number and one special character."
}

case object FirstNameHasSpecialCharacters extends DomainValidation {
  def errorMessage: String = "First name cannot contain spaces, numbers or special characters."
}

case object LastNameHasSpecialCharacters extends DomainValidation {
  def errorMessage: String = "Last name cannot contain spaces, numbers or special characters."
}

case object AgeIsInvalid extends DomainValidation {
  def errorMessage: String = "You must be aged 18 and not older than 75 to use our services."
}

sealed trait FormValidator {

  def validateUserName(userName: String): Either[DomainValidation, String] =
    Either.cond(userName.matches("^[a-zA-Z0-9]+$"), userName, UsernameHasSpecialCharacters)

  def validatePassword(password: String): Either[DomainValidation, String] =
    Either.cond(password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"),
                password,
                PasswordDoesNotMeetCriteria)

  def validateFirstName(firstName: String): Either[DomainValidation, String] =
    Either.cond(firstName.matches("^[a-zA-Z]+$"), firstName, FirstNameHasSpecialCharacters)

  def validateLastName(lastName: String): Either[DomainValidation, String] =
    Either.cond(lastName.matches("^[a-zA-Z]+$"), lastName, LastNameHasSpecialCharacters)

  def validateAge(age: Int): Either[DomainValidation, Int] =
    Either.cond(age >= 18 && age <= 75, age, AgeIsInvalid)

  def validateForm(
      username: String,
      password: String,
      firstName: String,
      lastName: String,
      age: Int): Either[DomainValidation, RegistrationData] =
    for {
      validatedUserName <- validateUserName(username)
      validatedPassword <- validatePassword(password)
      validatedFirstName <- validateFirstName(firstName)
      validatedLastName <- validateLastName(lastName)
      validatedAge <- validateAge(age)
    } yield RegistrationData(validatedUserName, validatedPassword, validatedFirstName, validatedLastName, validatedAge)

}

object FormValidator extends FormValidator

import cats.data._
import cats.data.Validated._
import cats.implicits._

sealed trait FormValidatorNel {

  type ValidationResult[A] = ValidatedNel[DomainValidation, A]

  private def validateUserName(userName: String): ValidationResult[String] =
    if (userName.matches("^[a-zA-Z0-9]+$")) userName.validNel else UsernameHasSpecialCharacters.invalidNel

  private def validatePassword(password: String): ValidationResult[String] =
    if (password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")) password.validNel
    else PasswordDoesNotMeetCriteria.invalidNel

  private def validateFirstName(firstName: String): ValidationResult[String] =
    if (firstName.matches("^[a-zA-Z]+$")) firstName.validNel else FirstNameHasSpecialCharacters.invalidNel

  private def validateLastName(lastName: String): ValidationResult[String] =
    if (lastName.matches("^[a-zA-Z]+$")) lastName.validNel else LastNameHasSpecialCharacters.invalidNel

  private def validateAge(age: Int): ValidationResult[Int] =
    if (age >= 18 && age <= 75) age.validNel else AgeIsInvalid.invalidNel

  def validateForm(
      username: String,
      password: String,
      firstName: String,
      lastName: String,
      age: Int): ValidationResult[RegistrationData] =
    (validateUserName(username),
     validatePassword(password),
     validateFirstName(firstName),
     validateLastName(lastName),
     validateAge(age)).mapN(RegistrationData)

}

object FormValidatorNel extends FormValidatorNel

object ValidatedDemo extends App {

  val result = FormValidator.validateForm(username = "fakeUs3rname",
                                          password = "password",
                                          firstName = "John",
                                          lastName = "Doe",
                                          age = 15)
  println(s"result: $result")

  val result1 = FormValidatorNel.validateForm(
    username = "Joe",
    password = "Passw0r$1234",
    firstName = "John",
    lastName = "Doe",
    age = 21
  )
  println(s"result: $result1")

  val result2 = FormValidatorNel.validateForm(
    username = "Joe%%%",
    password = "password",
    firstName = "John",
    lastName = "Doe",
    age = 21
  )
  println(s"result: $result2")
}
