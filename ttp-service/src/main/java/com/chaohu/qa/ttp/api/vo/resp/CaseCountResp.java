package com.chaohu.qa.ttp.api.vo.resp;

import com.chaohu.qa.ttp.db.po.CaseCountInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author wangmin
 * @date 2023/1/13 14:04
 */
@Data
@Accessors(chain = true)
public class CaseCountResp {
    private String env;
    private List<CaseCountInfo> caseCountList;
}
