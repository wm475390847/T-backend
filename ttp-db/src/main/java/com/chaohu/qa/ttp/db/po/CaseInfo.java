package com.chaohu.qa.ttp.db.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * case_info
 *
 * @author wangmin
 */
@Data
@Accessors(chain = true)
public class CaseInfo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用例名称
     */
    private String caseName;

    /**
     * 用例所有者
     */
    private String caseOwner;

    /**
     * 用例结果0：失败、1：成功
     */
    private Boolean caseResult;

    /**
     * 用例执行信息PASS：通过、其他为异常
     */
    private String caseReason;

    /**
     * 用例描述
     */
    private String caseDesc;

    /**
     * 这个此段需要存此用例覆盖的接口集合
     */
    private String apiJson;

    /**
     * 产品id
     */
    private Integer productId;

    /**
     * 产品名称
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 执行时间
     */
    private Date executeTime;

    /**
     * 环境：test、dev、pre
     */
    private String env;

    /**
     * ci执行的jobId
     */
    private Long ciJobId;

    private Date gmtCreate;

    private Date gmtModified;

    private static final long serialVersionUID = 1L;
}