package mass.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, extractUri}
import akka.http.scaladsl.server.ExceptionHandler
import akka.pattern.AskTimeoutException
import com.typesafe.scalalogging.StrictLogging
import helloscala.data.ApiResult
import helloscala.common.exception.HSException

/**
 * 基本异常处理函数
 * Created by yangbajing(yangbajing@gmail.com) on 2017-03-01.
 */
trait BaseExceptionPF extends StrictLogging {

  def exceptionHandlerPF: ExceptionHandler.PF = {
    case ex: Exception =>
      extractUri { uri =>
        val response = ex match {
          case e: HSException =>
            val t = e.getCause
            //            if (t != null) logger.warn(s"URI[$uri] ${e.toString}", t) else logger.warn(s"URI[$uri] ${e.toString}")
            logger.warn(s"Exception[${e.getClass.getSimpleName}] URI[$uri] ${e.getMessage}", t)
            (StatusCodes
               .getForKey(e.getHttpStatus)
               .getOrElse(StatusCodes.Conflict),
             ApiResult.error(e.getErrCode, e.getErrMsg, e.getData))

          case e: IllegalArgumentException =>
            logger.debug(s"Illegal Argument: ${e.getMessage}", e)
            (StatusCodes.BadRequest, ApiResult.error(StatusCodes.BadRequest.intValue, e.getLocalizedMessage))

          case e: AskTimeoutException =>
            logger.debug(s"Actor Timeout: ${e.getMessage}", e)
            (StatusCodes.GatewayTimeout, ApiResult.error(StatusCodes.GatewayTimeout.intValue, "请求超时"))

          case _ =>
            logger.error(s"请求无法正常处理，URI[$uri]", ex)
            (StatusCodes.InternalServerError, ApiResult.error(StatusCodes.InternalServerError.intValue, ex.getMessage))
        }
        import JacksonSupport._
        complete(response)
      }
  }

  final val exceptionHandler = ExceptionHandler(exceptionHandlerPF)
}
