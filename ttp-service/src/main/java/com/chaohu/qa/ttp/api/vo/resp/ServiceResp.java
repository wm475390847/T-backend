package com.chaohu.qa.ttp.api.vo.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author wangmin
 * @date 2022/6/24 10:00
 */
@Data
@Accessors(chain = true)
public class ServiceResp {

    private Integer id;
    private String serviceName;
    private List<ProductResp> products;
}
