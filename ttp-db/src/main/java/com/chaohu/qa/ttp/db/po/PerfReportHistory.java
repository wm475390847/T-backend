package com.chaohu.qa.ttp.db.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * perf_report_history
 *
 * @author wangmin
 */
@Data
@Accessors(chain = true)
public class PerfReportHistory implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
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