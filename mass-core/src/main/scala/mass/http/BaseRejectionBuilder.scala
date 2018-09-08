package mass.http

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.RejectionHandler.Builder
import akka.http.scaladsl.server._
import com.typesafe.scalalogging.StrictLogging
import helloscala.data.ApiResult

/**
 * Created by yangbajing(yangbajing@gmail.com) on 2017-03-01.
 */
trait BaseRejectionBuilder extends StrictLogging {

  import JacksonSupport._

  def rejectionBuilder: Builder =
    RejectionHandler
      .newBuilder()
      .handle {
        case MissingQueryParamRejection(parameterName) =>
          complete((BadRequest, ApiResult.error(BadRequest.intValue, s"请求参数 '$parameterName' 缺失")))

        case MissingCookieRejection(cookieName) =>
          val msg = s"无效的Cookie: $cookieName"
          logger.info(msg)
          complete((BadRequest, ApiResult.error(BadRequest.intValue, msg)))

        case ApiTokenRejection(message, cause) =>
          val msg = s"API Token校验失败：$message"
          logger.warn(msg, cause.orNull)
          complete((Unauthorized, ApiResult.error(Unauthorized.intValue, message)))

        case ForbiddenRejection(message, cause) =>
          val msg = s"权限禁止：$message"
          logger.warn(msg, cause.orNull)
          complete((Forbidden, ApiResult.error(Unauthorized.intValue, message)))

        case SessionRejection(message, cause) =>
          val msg = s"会话认证失败：$message"
          logger.warn(msg, cause.orNull)
          complete((Unauthorized, ApiResult.error(Unauthorized.intValue, message)))

        case AuthorizationFailedRejection =>
          val msg = "会话认证失败"
          logger.warn(msg)
          complete((Unauthorized, ApiResult.error(Unauthorized.intValue, msg)))

        case ValidationRejection(err, _) =>
          val msg = "数据校验失败： " + err
          logger.info(msg)
          complete((BadRequest, ApiResult.error(InternalServerError.intValue, msg)))
      }
      .handleAll[MethodRejection] { methodRejections =>
        val description =
          methodRejections.map(_.supported.name).mkString(" or ")
        val msg = s"不支持的方法！当前支持：$description!"
        logger.info(msg)
        complete((MethodNotAllowed, ApiResult.error(MethodNotAllowed.intValue, msg)))
      }
      .handleNotFound {
        extractUri { uri =>
          val msg = s"URI: $uri 未找到！"
          logger.info(msg)
          complete((NotFound, ApiResult.error(NotFound.intValue, msg)))
        }
      }
      .handle {
        case rejection =>
          logger.info(rejection.toString)
          complete((BadRequest, ApiResult.error(BadRequest.intValue, rejection.toString)))
      }

  final val rejectionHandler: RejectionHandler = rejectionBuilder.result()

}
