package com.chaohu.qa.ttp.api.vo.resp;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wangmin
 * @date 2022/6/24 09:34
 */
@Data
@Accessors(chain = true)
public class ProductResp {

    private Integer id;
    private String productName;
    private String serviceName;
}
