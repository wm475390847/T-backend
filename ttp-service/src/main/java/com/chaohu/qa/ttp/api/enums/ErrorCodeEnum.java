package com.chaohu.qa.ttp.api.enums;

import lombok.Getter;

/**
 * @author wangmin
 */
public enum ErrorCodeEnum {
    /**
     * 成功
     */
    SUCCESS("200", "成功"),

    /**
     * 系统错误，一般为空指针等未获取错误
     */
    SYSTEM_ERROR("1000", "开发人员吃饭去了，很着急的话请电话联系120"),

    /**
     * 用户错误
     */
    TOKEN_IS_NULL("400", "获取的token为空"),
    USER_IS_NULL("400", "用户信息为空"),

    /**
     * 自定义错误
     */
    SYSTEM_FAIL("1001", null),
    DB_ERROR("1002", "数据库错误"),
    TH_INVOKE_FAIL("1003", "三方接口访问失败"),


    /**
     * 素材错误
     */
    M_NOT_FOUND("1101", "素材不存在"),
    M_CREATE_FAIL("1101", "素材创建失败"),
    M_DELETE_FAIL("1102", "素材删除失败"),
    M_UPDATE_FAIL("1103", "素材修改失败"),

    /**
     * 用例错误
     */
    C_NOT_FOUND("1200", "用例不存在"),
    C_CREATE_FAIL("1201", "用例创建失败"),
    C_DELETE_FAIL("1202", "用例删除失败"),
    C_UPDATE_FAIL("1203", "用例修改失败"),
    C_REPETITION_FAIL("1204", "用例已存在"),
    C_VIDEO_COUNT_FAIL("1205", "视频数量错误"),
    C_IS_BLANK("1206", "用例为空"),


    P_CREATE_FAIL("2001", "产品创建失败"),
    P_UPDATE_FAIL("2002", "产品修改失败"),
    P_DELETE_FAIL("2003", "产品删除失败"),
    P_ID_IS_NULL("2004", "产品id为空"),

    PIC_CREATE_FAIL("3001", "图片保存失败"),
    PIC_UPDATE_FAIL("3002", "图片更新失败"),
    PIC_CUTOUT_FAIL("3003", "图片截取失败"),
    PIC_PUT_OSS_FAIL("3004", "图片上传OSS失败"),
    PIC_DOWNLOAD_OSS_FAIL("3005", "图片下载失败"),
    PIC_IS_NULL("3006", "图片不存在"),


    PERF_CREATE_FAIL("4001", "性能测试创建失败"),
    PERF_UPDATE_FAIL("4002", "性能测试修改失败"),
    PERF_DELETE_FAIL("4003", "性能测试删除失败"),
    PERF_IS_NULL("4004", "性能测试不存在"),
    PERF_STOP_FAIL("4005", "性能测试停止失败"),
    PREF_START_FAIL("4006", "性能测试启动失败"),

    FILE_DOWNLOAD_FAIL("5001", "文件下载失败"),
    FILE_UPLOAD_FAIL("5002", "文件上传失败"),


    HISTORY_CREATE_FAIL("6001", "执行记录创建失败"),
    HISTORY_UPDATE_FAIL("6002", "执行记录更新失败"),

    ;

    ErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Getter
    private final String code;
    @Getter
    private final String desc;
}
