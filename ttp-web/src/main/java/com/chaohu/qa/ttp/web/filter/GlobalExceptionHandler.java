package com.chaohu.qa.ttp.web.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.chaohu.qa.ttp.api.enums.ErrorCodeEnum;
import com.chaohu.qa.ttp.api.exception.ServiceException;
import com.chaohu.qa.ttp.web.Response.Result;
import com.chaohu.qa.ttp.web.util.ContextInfo;
import com.chaohu.qa.ttp.web.util.RequestContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * ControllerAdvice（Controller增强，自spring3.2的时候推出）全局异常处理
 * <p>
 * 主要是用于全局的异常拦截和处理,这里的异常可以使自定义异常也可以是JDK里面的异常
 * 用于处理当数据库事务业务和预期不同的时候抛出封装后的异常，进行数据库事务回滚，并将异常的显示给用户
 * </p>
 *
 * @author wangmin
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * javax.validation.constraints抛出的异常
     *
     * @param exception 异常
     * @return Object
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object bindException(MethodArgumentNotValidException exception) {
        String message = Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage();
        ErrorCodeEnum error = ErrorCodeEnum.SYSTEM_ERROR;
        return exceptionFormat(error.getCode(), message, exception);
    }

    /**
     * 系统可预期异常
     *
     * @param exception 异常
     * @return Object
     */
    @ExceptionHandler(ServiceException.class)
    public Object servicesException(ServiceException exception) {
        return exceptionFormat(exception.getCode(), exception.getMessage(), exception);
    }

    /**
     * mybatis异常
     *
     * @param exception 异常
     * @return Object
     */
    @ExceptionHandler(MyBatisSystemException.class)
    public Object myBatisSystemException(MyBatisSystemException exception) {
        ErrorCodeEnum error = ErrorCodeEnum.DB_ERROR;
        return exceptionFormat(error.getCode(), error.getDesc(), exception);
    }

    /**
     * 非预期异常
     *
     * @param exception 异常
     * @return Object
     */
    @ExceptionHandler(Exception.class)
    public Object exception(Exception exception) {
        ErrorCodeEnum error = ErrorCodeEnum.SYSTEM_ERROR;
        return exceptionFormat(error.getCode(), error.getDesc(), exception);
    }

    /**
     * 异常信息打印并且返回result
     *
     * @param errorCode 错误码
     * @param errorMsg  错误信息
     * @param exception 错误
     * @param <T>       T
     * @return Result<?>
     */
    private <T extends Throwable> Result<?> exceptionFormat(String errorCode, String errorMsg, T exception) {
        ContextInfo contextInfo = RequestContextUtil.get();
        String requestId = contextInfo.getRequestId();

        String errMsg = StrUtil.format(
                "HTTP请求异常: requestId: {}, class: {}, method: {}, param: {}, errorCode: {}, errorMsg: {}, exception: {}",
                requestId, contextInfo.getRequestClass(), contextInfo.getRequestMethod(), contextInfo.getRequestParam(),
                errorCode, errorMsg, ExceptionUtil.stacktraceToString(exception));
        log.error("异常:\n {}", errMsg);

        return Result.fail(errorCode, errorMsg).setRequestId(requestId);
    }
}
