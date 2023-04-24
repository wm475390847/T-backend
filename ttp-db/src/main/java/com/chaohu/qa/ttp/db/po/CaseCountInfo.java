package com.chaohu.qa.ttp.db.po;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wangmin
 * @date 2023/1/13 14:04
 */
@Data
@Accessors(chain = true)
public class CaseCountInfo {

    private String productName;
    private String env;
    private Long productId;
    private Long count;
}
