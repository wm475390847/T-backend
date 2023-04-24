package com.chaohu.qa.ttp.api.exception;

import com.chaohu.qa.ttp.api.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wangmin
 * @date 2021/10/28 2:26 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceException extends RuntimeException {
    private String code;
    private String message;

    public ServiceException(ErrorCodeEnum errorCodeEnum) {
        this.code = errorCodeEnum.getCode();
        this.message = errorCodeEnum.getDesc();
    }

    public ServiceException(String message) {
        this.code = ErrorCodeEnum.SYSTEM_FAIL.getCode();
        this.message = message;
    }
}
