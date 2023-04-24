package com.chaohu.qa.ttp.api.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建产品
 *
 * @author wangmin
 * @date 2022/9/21 13:58
 */
@Data
@Accessors(chain = true)
public class CreateProductReq {

    private Integer id;

    @NotBlank(message = "产品名称不能为空")
    private String productName;

    @NotNull(message = "产品所属业务不能为空")
    private Integer serviceId;
}
