package com.chaohu.qa.ttp.db.po;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * performance_info
 *
 * @author wangmin
 */
@Data
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class PerformanceInfo implements Serializable {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 性能测试名称
     */
    private String performanceName;

    /**
     * 请求数据
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject requestData;

    /**
     * 线程数据
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
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
     * 是否删除
     */
    private Boolean deleted;

    /**
     * 状态：1：等待中；2：运行中；3：已完成；
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