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

package helloscala.data;

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
