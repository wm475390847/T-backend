package com.chaohu.qa.ttp.api.vo.resp;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * performance_info
 *
 * @author wangmin
 */
@Data
@Accessors(chain = true)
public class PerformanceInfoResp implements Serializable {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 性能测试名称
     */
    private String performanceName;

    /**
     * 请求数据
     */
    private JSONObject requestData;

    /**
     * 线程数据
     */
    private JSONObject threadData;

    /**
     * jxm文件路径
     */
    private String jmxPath;

    /**
     * 执行时间
     */
    private Date executeTime;

    /**
     * 执行耗时
     */
    private Long elapsedTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    private static final long serialVersionUID = 1L;
}