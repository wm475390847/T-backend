package com.chaohu.qa.ttp.api.vo.req;

import com.chaohu.qa.ttp.api.dto.RequestDataDTO;
import com.chaohu.qa.ttp.api.dto.ThreadDataDTO;
import lombok.Data;

/**
 * @author wangmin
 * @date 2023/3/30 17:54
 */
@Data
public class PerformanceCreateReq {

    private String performanceName;
    private RequestDataDTO requestData;
    private ThreadDataDTO threadGroupData;
}
