package com.chaohu.qa.ttp.api.vo.req;

import lombok.Data;

import java.util.List;

/**
 * @author wangmin
 * @date 2022/8/15 11:11
 */
@Data
public class CaseListReq {

    private Integer productId;

    private List<Long> caseIds;
}
