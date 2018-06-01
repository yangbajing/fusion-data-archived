/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package helloscala.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import helloscala.common.ErrCodes;
import helloscala.common.data.IApiResult;

/**
 * Exception 基类
 * Created by yangbajing(yangbajing@gmail.com) on 2017-02-27.
 */
@JsonIgnoreProperties(value = {"suppressed", "localizedMessage", "message", "stackTrace", "cause"})
public class HSException extends RuntimeException implements IApiResult<Object> {

    /**
     * "错误代码"
     */
    private Integer errCode;

    private Object data;

    @JsonIgnore
    protected int httpStatus = ErrCodes.INTERNAL_ERROR;

    public HSException() {
    }

    public HSException(Integer errCode, String errMsg) {
        this(errCode, errMsg, null);
        this.errCode = errCode;
//        this.errMsg = errMsg;
    }

    public HSException(Integer errCode, String errMsg, Throwable cause) {
        super("[" + errCode + "] " + errMsg, cause);
        this.errCode = errCode;
    }

    protected HSException(Integer errCode, String errMsg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super("[" + errCode + "] " + errMsg, cause, enableSuppression, writableStackTrace);
        this.errCode = errCode;
    }

    public static HSException success() {
        return new HSException(0, "");
    }

    public static HSException error(String errMsg) {
        return new HSException(ErrCodes.UNKNOWN, errMsg);
    }

    public static HSException error(int errCode, String errMsg) {
        return new HSException(errCode, errMsg);
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    @Override
    public String getErrMsg() {
        return this.getMessage();
    }

//    public void setErrMsg(String errMsg) {
//    }

    @Override
    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HSException{" +
                "errCode=" + errCode +
                ", errMsg='" + this.getMessage() + '\'' +
//                ", data=" + data +
                '}';
    }
}
