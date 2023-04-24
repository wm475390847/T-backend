package com.chaohu.qa.ttp.db.po;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * product_info
 *
 * @author wangmin
 */
@Data
@Accessors(chain = true)
public class ProductInfo implements Serializable {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 业务线id
     */
    private Integer serviceId;

    /**
     * 业务线名称
     */
    @TableField(exist = false)
    private String serviceName;

    /**
     * 是否已经删除
     */
    private Boolean deleted;

    private static final long serialVersionUID = 1L;
}