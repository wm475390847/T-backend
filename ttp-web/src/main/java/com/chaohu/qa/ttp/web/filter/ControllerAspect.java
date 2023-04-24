package com.chaohu.qa.ttp.web.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chaohu.qa.ttp.web.util.ContextInfo;
import com.chaohu.qa.ttp.web.util.RequestContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author wangmin
 */
@Slf4j
@Aspect
@Component
public class ControllerAspect {

    /**
     * execution(* com.xxx.xxx.web.controller..*.*(..))启动超级慢
     * within(com.xxx.xxx.web.controller.*.*)启动速度快
     */
    @Pointcut("within(com.chaohu.qa.ttp.web.controller.*)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取方法传入参数
        Object[] params = joinPoint.getArgs();
        String requestParams = JSON.toJSONString(params, SerializerFeature.IgnoreErrorGetter);

        String clazzName = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        ContextInfo contextInfo = RequestContextUtil.get();
        String requestId = contextInfo.getRequestId();

        contextInfo.setRequestClass(clazzName);
        contextInfo.setRequestMethod(methodName);
        contextInfo.setRequestParam(requestParams);

        long startTime = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        String responseParams = JSON.toJSONString(proceed, SerializerFeature.IgnoreErrorGetter);
        //打印拦截日志，webStart方法不进行打印
        if (methodName.contains("webStart")) {
            return proceed;
        }
        log.info("class: {}, method: {}, parameters: {}, response: {}, requestId:{}, time: {}ms",
                clazzName, methodName, requestParams, responseParams, requestId,
                System.currentTimeMillis() - startTime);
        return proceed;
    }
}
