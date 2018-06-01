/*
 * Copyright (c) Yangbajing 2018
 *
 * This is the custom License of Yangbajing
 */

package helloscala.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by yangbajing(yangbajing@gmail.com) on 2017-03-30.
 */
public interface IApiResult<T> {
    Integer getErrCode();

    String getErrMsg();

    T getData();

    @JsonIgnore
    default boolean isSuccess() {
        Integer errCode = getErrCode();
        return errCode == null || errCode == 0;
    }

}
