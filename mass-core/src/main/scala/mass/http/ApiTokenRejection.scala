package mass.http

import akka.http.scaladsl.server.RejectionWithOptionalCause

/**
 * Created by yangbajing(yangbajing@gmail.com) on 2017-03-30.
 */
case class ApiTokenRejection(message: String, cause: Option[Throwable] = None)
  extends akka.http.javadsl.server.AuthorizationFailedRejection
  with RejectionWithOptionalCause
