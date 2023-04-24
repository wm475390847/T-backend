package com.chaohu.qa.ttp.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;

import java.util.Map;

/**
 * @author wangmin
 * @date 2023/3/30 17:27
 */
@Data
@Accessors(chain = true)
public class RequestDataDTO {

    /**
     * 接口url
     */
    private String url;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * application/json的请求体
     */
    private String requestBody;

    /**
     * 请求头
     */
    private Map<String, String> headers;

    /**
     * 请求方式
     */
    private HttpMethod method;
}
