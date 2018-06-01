/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package helloscala.common.data;

import java.util.Objects;

/**
 * Api Response Result
 * Created by yangbajing(yangbajing@gmail.com) on 2017-03-01.
 */
public class ApiResult implements IApiResult<Object> {
    private Integer errCode = 0;
    private String errMsg = null;

    private Object data = null;

    public ApiResult() {

    }

    public ApiResult(Integer errCode, String errMsg, Object data) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }

    public static ApiResult success() {
        return success(null);
    }

    public static ApiResult success(Object data) {
        return new ApiResult(0, null, data);
    }

    public static ApiResult error(Integer errCode, String errMsg) {
        return error(errCode, errMsg, null);
    }

    public static ApiResult error(Integer errCode, String errMsg, Object data) {
        return new ApiResult(errCode, errMsg, data);
    }

    public Integer getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public Object getData() {
        return data;
    }

    public ApiResult setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ApiResult)) return false;
        ApiResult apiResult = (ApiResult) o;
        return Objects.equals(getErrCode(), apiResult.getErrCode()) &&
                Objects.equals(getErrMsg(), apiResult.getErrMsg()) &&
                Objects.equals(getData(), apiResult.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getErrCode(), getErrMsg(), getData());
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "errCode=" + errCode +
                ", errMsg='" + errMsg + '\'' +
                ", data=" + data +
                '}';
    }

}
