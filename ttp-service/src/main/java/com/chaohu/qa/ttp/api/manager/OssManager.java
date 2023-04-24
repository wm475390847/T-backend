package com.chaohu.qa.ttp.api.manager;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.aliyuncs.sts.model.v20150401.AssumeRoleRequest;
import com.aliyuncs.sts.model.v20150401.AssumeRoleResponse;
import com.chaohu.qa.ttp.api.dto.OssConfigDTO;
import com.chaohu.qa.ttp.api.enums.ErrorCodeEnum;
import com.chaohu.qa.ttp.api.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Date;

/**
 * OSS管理
 *
 * @author wangmin
 * @date 2022/8/8 10:32
 */
@Slf4j
@Component
public class OssManager {

    @Value("${oss.endpoint}")
    private String endpoint;
    @Value("${oss.ak}")
    private String ak;
    @Value("${oss.sk}")
    private String sk;
    @Value("${oss.bucketName}")
    private String bucketName;
    @Value("${oss.meeting.folder}")
    private String ossMeetingFolder;
    @Value("${oss.sts.role-arn}")
    private String roleArn;
    @Value("${oss.sts.roleSessionName}")
    private String roleSessionName;
    @Value("${oss.sts.end-point}")
    private String stsEndPoint;
    @Value("${oss.sts.policy}")
    private String policy;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        ossClient = new OSSClientBuilder().build(endpoint, ak, sk);
    }

    @PreDestroy
    public void close() {
        ossClient.shutdown();
    }

    public String getBashOssPath() {
        return endpoint + "/" + bucketName + "/";
    }

    /**
     * 从本地上传oss
     *
     * @param file          需要上传的文件
     * @param ossPrefixPath OSS上存储的前缀路径
     * @return ossPath
     */
    public String upload(File file, String ossPrefixPath) {
        try {
            if (!ossPrefixPath.endsWith("/")) {
                ossPrefixPath += "/";
            }
            log.info("上传文件: {} -> {}", file.getAbsolutePath(), ossPrefixPath);
            String objectKey = ossPrefixPath + file.getName();
            ossClient.putObject(bucketName, objectKey, new FileInputStream(file));
            log.info("上传成功");
            return endpoint + "/" + bucketName + "/" + objectKey;
        } catch (FileNotFoundException fe) {
            log.error("FileNotFoundException: {}", fe.getMessage());
            throw new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAIL);
        }
    }

    /**
     * 递归上传本地文件夹到 OSS 上。
     *
     * @param localFolder   要上传的本地文件夹。
     * @param ossPrefixPath OSS上存储的前缀路径。
     */
    public void uploadFolder(File localFolder, String ossPrefixPath) {
        if (localFolder.isDirectory()) {
            if (!ossPrefixPath.endsWith("/")) {
                ossPrefixPath += "/";
            }
            // 遍历本地文件夹，并上传文件到 OSS 上。
            File[] files = localFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String ossObjectKey = ossPrefixPath + file.getName();
                    if (file.isDirectory()) {
                        // 如果是文件夹，递归上传。
                        uploadFolder(file, ossObjectKey);
                    } else {
                        // 如果是文件，直接上传到 OSS 上。
                        PutObjectRequest putRequest = new PutObjectRequest(bucketName, ossObjectKey, file);
                        ossClient.putObject(putRequest);
                        log.info("上传文件: {} -> {}", file.getAbsolutePath(), ossObjectKey);
                    }
                }
            }
        }
    }

    /**
     * 从oss下载到本地
     *
     * @param ossFilePath   云端存放文件的路径，由文件所在文件夹+文件名称组成
     * @param localFilePath 本地存放文件的路径
     * @return 本地文件
     */
    public File download(String ossFilePath, String localFilePath) {
        try {
            log.info("下载文件: {} -> {}", ossFilePath, localFilePath);
            File file = new File(localFilePath);
            ossClient.getObject(new GetObjectRequest(bucketName, ossFilePath), file);
            log.info("下载成功");
            return file;
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            e.printStackTrace();
            throw new ServiceException(ErrorCodeEnum.FILE_DOWNLOAD_FAIL);
        }
    }

    /**
     * 加签处理
     *
     * @param ossPath oss路径
     * @return 加签之后的地址
     */
    public String addSign(String ossPath) {
        if (StringUtils.isBlank(ossPath)) {
            return null;
        }
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60);
        URL url = ossClient.generatePresignedUrl(bucketName, getObjectName(ossPath), expiration);
        return url.toString();
    }

    /**
     * 获取oss配置
     *
     * @param business 业务
     * @return OssConfigDTO
     */
    public OssConfigDTO getOssConfig(String business) throws ClientException {
        DefaultProfile.addEndpoint("", "Sts", stsEndPoint);
        IClientProfile profile = DefaultProfile.getProfile("", ak, sk);
        DefaultAcsClient client = new DefaultAcsClient(profile);
        String uploadPath = ossMeetingFolder + "/" + business;
        String realResource = "acs:oss:*:*:" + bucketName + "/" + uploadPath + "/" + "*";
        String newPolicy = policy.replace("policyResource", realResource);
        log.info("newPolicy: {}", newPolicy);
        final AssumeRoleRequest request = new AssumeRoleRequest();
        request.setSysMethod(MethodType.POST);
        request.setRoleArn(roleArn);
        request.setPolicy(newPolicy);
        request.setRoleSessionName(roleSessionName);
        Long durationSeconds = 3600L;
        request.setDurationSeconds(durationSeconds);
        final AssumeRoleResponse response = client.getAcsResponse(request);
        AssumeRoleResponse.Credentials credentials = response.getCredentials();
        return new OssConfigDTO()
                .setAccessKeyId(credentials.getAccessKeyId())
                .setAccessKeySecret(credentials.getAccessKeySecret())
                .setSecurityToken(credentials.getSecurityToken())
                .setExpiration(credentials.getExpiration())
                .setBucketName(bucketName)
                .setEndPoint(endpoint)
                .setUploadPath(uploadPath);
    }

    /**
     * 获取objectName
     *
     * @param ossPath oss地址
     * @return key
     */
    private String getObjectName(String ossPath) {
        return ossPath.split(bucketName + "/")[1];
    }
}
