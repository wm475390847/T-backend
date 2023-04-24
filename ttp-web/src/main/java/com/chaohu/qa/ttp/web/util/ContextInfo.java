package com.chaohu.qa.ttp.web.util;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * 切面拦截消息
 *
 * @author wangmin
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ContextInfo {

    private String project;
    private String requestId;
    private String requestUri;
    private String requestClass;
    private String requestMethod;
    private String requestParam;
    private String channelId;

    public String getRequestId() {
        String traceId = StringUtils.trimToEmpty(MDC.get("traceId"));
        return StrUtil.isBlank(traceId) ? requestId : traceId;
    }
}