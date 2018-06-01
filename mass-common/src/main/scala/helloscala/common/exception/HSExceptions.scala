/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package helloscala.common.exception

import helloscala.common.ErrCodes

case class HSAcceptedWarning(
    errMsg: String,
    data: AnyRef = null,
    errCode: Int = ErrCodes.ACCEPTED,
    cause: Throwable = null) extends HSException(errCode, errMsg, cause) {
  httpStatus = ErrCodes.ACCEPTED
  if (data != null) setData(data)
}

case class HSBadRequestException(
    errMsg: String,
    data: AnyRef = null,
    errCode: Int = ErrCodes.BAD_REQUEST,
    cause: Throwable = null) extends HSException(errCode, errMsg, cause) {
  httpStatus = ErrCodes.BAD_REQUEST
  if (data != null) setData(data)
}

case class HSUnauthorizedException(
    errMsg: String,
    data: AnyRef = null,
    errCode: Int = ErrCodes.UNAUTHORIZED,
    cause: Throwable = null) extends HSException(errCode, errMsg, cause) {
  httpStatus = ErrCodes.UNAUTHORIZED
  if (data != null) setData(data)
}

case class HSNoContentException(
    errMsg: String,
    data: AnyRef = null,
    errCode: Int = ErrCodes.NO_CONTENT,
    cause: Throwable = null) extends HSException(errCode, errMsg, cause) {
  httpStatus = ErrCodes.NO_CONTENT
  if (data != null) setData(data)
}

case class HSForbiddenException(
    errMsg: String,
    data: AnyRef = null,
    errCode: Int = ErrCodes.FORBIDDEN,
    cause: Throwable = null) extends HSException(errCode, errMsg, cause) {
  httpStatus = ErrCodes.FORBIDDEN
  if (data != null) setData(data)
}

case class HSNotFoundException(
    errMsg: String,
    data: AnyRef = null,
    errCode: Int = ErrCodes.NOT_FOUND,
    cause: Throwable = null) extends HSException(errCode, errMsg, cause) {
  httpStatus = ErrCodes.NOT_FOUND
  if (data != null) setData(data)
}

case class HSConfigurationException(
    errMsg: String,
    data: AnyRef = null,
    errCode: Int = ErrCodes.NOT_FOUND_CONFIG,
    cause: Throwable = null) extends HSException(errCode, errMsg, cause) {
  httpStatus = ErrCodes.NOT_FOUND
  if (data != null) setData(data)
}

case class HSConflictException(
    errMsg: String,
    data: AnyRef = null,
    errCode: Int = ErrCodes.CONFLICT,
    cause: Throwable = null) extends HSException(errCode, errMsg, cause) {
  httpStatus = ErrCodes.CONFLICT
  if (data != null) setData(data)
}

case class HSNotImplementedException(
    errMsg: String,
    data: AnyRef = null,
    errCode: Int = ErrCodes.NOT_IMPLEMENTED,
    cause: Throwable = null) extends HSException(errCode, errMsg, cause) {
  httpStatus = ErrCodes.NOT_IMPLEMENTED
  if (data != null) setData(data)
}

case class HSInternalErrorException(
    errMsg: String,
    data: AnyRef = null,
    errCode: Int = ErrCodes.INTERNAL_ERROR,
    cause: Throwable = null) extends HSException(errCode, errMsg, cause) {
  httpStatus = ErrCodes.INTERNAL_ERROR
  if (data != null) setData(data)
}
