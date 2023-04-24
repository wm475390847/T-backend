package com.chaohu.qa.ttp.api.vo.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * perf_report_history
 *
 * @author wangmin
 */
@Data
@Accessors(chain = true)
public class PerfReportHistoryResp implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 关联的性能测试id
     */
    private Integer perfId;

    /**
     * 报告地址
     */
    private String reportPath;

    /**
     * 执行耗时
     */
    private Long elapsedTime;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    private static final long serialVersionUID = 1L;
}