/*
 * Copyright 2018 羊八井(yangbajing)（杨景）
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helloscala.common.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import helloscala.common.ErrCodes;
import helloscala.data.IApiResult;

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
