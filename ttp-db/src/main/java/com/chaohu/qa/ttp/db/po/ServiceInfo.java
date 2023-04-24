package com.chaohu.qa.ttp.db.po;

import java.io.Serializable;

import lombok.Data;

/**
 * service_info
 *
 * @author wangmin
 */
@Data
public class ServiceInfo implements Serializable {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 业务线名称
     */
    private String serviceName;

    private static final long serialVersionUID = 1L;
}