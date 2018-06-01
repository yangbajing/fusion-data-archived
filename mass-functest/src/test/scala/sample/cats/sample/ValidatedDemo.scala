//package sample.cats.sample
//
//import cats.data._
//import cats.data.Validated._
//import cats.implicits._
//import cats.syntax.either._
//
//object ValidatedDemo extends App {
//
//  def validateUserName(userName: String): Validated[DomainValidation, String] = FormValidator.validateUserName(userName).toValidated
//
//  def validatePassword(password: String): Validated[DomainValidation, String] = FormValidator.validatePassword(password).toValidated
//
//  def validateFirstName(firstName: String): Validated[DomainValidation, String] = FormValidator.validateFirstName(firstName).toValidated
//
//  def validateLastName(lastName: String): Validated[DomainValidation, String] = FormValidator.validateLastName(lastName).toValidated
//
//  def validateAge(age: Int): Validated[DomainValidation, Int] = FormValidator.validateAge(age).toValidated
//
//  def validateForm(username: String, password: String, firstName: String, lastName: String, age: Int): Validated[DomainValidation, RegistrationData] = {
//    for {
//      validatedUserName <- validateUserName(username)
//      validatedPassword <- validatePassword(password)
//      validatedFirstName <- validateFirstName(firstName)
//      validatedLastName <- validateLastName(lastName)
//      validatedAge <- validateAge(age)
//    } yield RegistrationData(validatedUserName, validatedPassword, validatedFirstName, validatedLastName, validatedAge)
//  }
//
//  val result = validateForm("yangbajing", "yangbajing", "杨", "景", 32)
//  println(result)
//}
//
//final case class RegistrationData(username: String, password: String, firstName: String, lastName: String, age: Int)
//
//sealed trait DomainValidation {
//  def errorMessage: String
//}
//
//case object UsernameHasSpecialCharacters extends DomainValidation {
//  def errorMessage: String = "Username cannot contain special characters."
//}
//
//case object PasswordDoesNotMeetCriteria extends DomainValidation {
//  def errorMessage: String = "Password must be at least 10 characters long, including an uppercase and a lowercase letter, one number and one special character."
//}
//
//case object FirstNameHasSpecialCharacters extends DomainValidation {
//  def errorMessage: String = "First name cannot contain spaces, numbers or special characters."
//}
//
//case object LastNameHasSpecialCharacters extends DomainValidation {
//  def errorMessage: String = "Last name cannot contain spaces, numbers or special characters."
//}
//
//case object AgeIsInvalid extends DomainValidation {
//  def errorMessage: String = "You must be aged 18 and not older than 75 to use our services."
//}
//
//sealed trait FormValidator {
//  def validateUserName(userName: String): Either[DomainValidation, String] =
//    Either.cond(
//      userName.matches("^[a-zA-Z0-9]+$"),
//      userName,
//      UsernameHasSpecialCharacters
//    )
//
//  def validatePassword(password: String): Either[DomainValidation, String] =
//    Either.cond(
//      password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$"),
//      password,
//      PasswordDoesNotMeetCriteria
//    )
//
//  def validateFirstName(firstName: String): Either[DomainValidation, String] =
//    Either.cond(
//      firstName.matches("^[a-zA-Z]+$"),
//      firstName,
//      FirstNameHasSpecialCharacters
//    )
//
//  def validateLastName(lastName: String): Either[DomainValidation, String] =
//    Either.cond(
//      lastName.matches("^[a-zA-Z]+$"),
//      lastName,
//      LastNameHasSpecialCharacters
//    )
//
//  def validateAge(age: Int): Either[DomainValidation, Int] =
//    Either.cond(
//      age >= 18 && age <= 75,
//      age,
//      AgeIsInvalid
//    )
//
//  def validateForm(username: String, password: String, firstName: String, lastName: String, age: Int): Either[DomainValidation, RegistrationData] = {
//
//    for {
//      validatedUserName <- validateUserName(username)
//      validatedPassword <- validatePassword(password)
//      validatedFirstName <- validateFirstName(firstName)
//      validatedLastName <- validateLastName(lastName)
//      validatedAge <- validateAge(age)
//    } yield RegistrationData(validatedUserName, validatedPassword, validatedFirstName, validatedLastName, validatedAge)
//  }
//
//}
//
//object FormValidator extends FormValidator
