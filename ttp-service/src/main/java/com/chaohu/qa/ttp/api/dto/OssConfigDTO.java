package com.chaohu.qa.ttp.api.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wangmin
 * @date 2022/11/22 16:36
 */
@Data
@Accessors(chain = true)
public class OssConfigDTO {
    private String securityToken;
    private String accessKeySecret;
    private String accessKeyId;
    private String expiration;
    private String bucketName;
    private String endPoint;
    private String uploadPath;
    private String fileId;
}
