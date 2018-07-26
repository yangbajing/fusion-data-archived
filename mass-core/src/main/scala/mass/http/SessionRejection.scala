package mass.http

import akka.http.scaladsl.server.RejectionWithOptionalCause

/**
  * 权限拒绝，403
  * @param message 错误消息
  * @param cause 可选的异常
  */
case class ForbiddenRejection(message: String, cause: Option[Throwable] = None)
    extends akka.http.javadsl.server.AuthorizationFailedRejection
    with RejectionWithOptionalCause

/**
  * 会话认证拒绝，401
  * @param message 错误消息
  * @param cause 可选的异常
  */
case class SessionRejection(message: String, cause: Option[Throwable] = None)
    extends akka.http.javadsl.server.AuthorizationFailedRejection
    with RejectionWithOptionalCause
