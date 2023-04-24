package com.chaohu.qa.ttp.web.Response;

import com.chaohu.qa.ttp.api.enums.ErrorCodeEnum;
import com.chaohu.qa.ttp.web.util.RequestContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 结果类
 *
 * @author wangmin
 */
@Data
@Slf4j
public class Result<T> {
    private boolean success;
    private String code;
    private String message;
    private T data;
    private String requestId = "";

    public Result(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(T data) {
        this(true, "200", "success", data);
    }

    public Result(String message) {
        this(true, "200", message, null);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        String requestId = RequestContextUtil.get().getRequestId();
        return new Result<>(data).setRequestId(requestId);
    }

    public static <T> Result<T> success(String message) {
        String requestId = RequestContextUtil.get().getRequestId();
        return (Result<T>) new Result<>(message).setRequestId(requestId);
    }

    public static <T> Result<T> fail(String code, String message) {
        return new Result<>(false, code, message, null);
    }

    public static <T> Result<T> fail(ErrorCodeEnum errorCodeEnum) {
        return new Result<>(false, errorCodeEnum.getCode(), errorCodeEnum.getDesc(), null);
    }

    public static <T> Result<T> fail(ErrorCodeEnum errorCodeEnum, T data) {
        return new Result<>(false, errorCodeEnum.getCode(), errorCodeEnum.getDesc(), data);
    }

    public Result<T> setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}
