package com.chaohu.qa.ttp.web.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 请求体工具类
 *
 * @author wangmin
 */
public class RequestContextUtil {
    private static final ThreadLocal<ContextInfo> THREAD_LOCAL = new ThreadLocal<>();

    public static ContextInfo init() {
        ContextInfo contextInfo = new ContextInfo().setRequestId(IdUtil.fastSimpleUUID());
        put(contextInfo);
        return contextInfo;
    }

    public static ContextInfo get() {
        ContextInfo contextInfo = THREAD_LOCAL.get();
        return Objects.isNull(contextInfo) ? init() : contextInfo;
    }

    public static void put(ContextInfo contextInfo) {
        THREAD_LOCAL.set(contextInfo);
    }

    public static void update(ContextInfo newContextInfo) {
        ContextInfo oldContextInfo = get();
        // 不为空的属性进行拷贝
        BeanUtil.copyProperties(newContextInfo, oldContextInfo,
                CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        put(oldContextInfo);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    public static void setRequestId(String requestId) {
        if (StringUtils.isNotBlank(requestId)) {
            update(new ContextInfo().setRequestId(requestId));
        }
    }
}
