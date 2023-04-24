package com.chaohu.qa.ttp.api.service;

import com.chaohu.qa.ttp.api.vo.req.CreateCaseReq;
import com.chaohu.qa.ttp.api.vo.resp.CaseCountResp;
import com.chaohu.qa.ttp.api.vo.resp.CaseInfoResp;
import com.chaohu.qa.ttp.db.config.CustomPage;

import java.util.List;

/**
 * @author wangmin
 * @date 2022/7/15 11:52
 */
public interface ICaseInfoService {

    /**
     * 用例列表
     *
     * @param pageNo     页码
     * @param pageSize   页大小
     * @param caseResult 用例结果
     * @param productId  产品id
     * @param env        环境
     * @param caseOwner  case归属
     * @param caseName   用例名称
     * @return 列表
     */
    CustomPage<CaseInfoResp> list(int pageNo, int pageSize, Boolean caseResult, Integer productId, String env, String caseOwner, String caseName);

    /**
     * 用例列表
     *
     * @param productId 产品id
     * @return 列表
     */
    List<CaseInfoResp> list(Integer productId);

    /**
     * 用例列表
     *
     * @param caseIds 用例id集合
     * @return 列表
     */
    List<CaseInfoResp> list(List<Long> caseIds);

    /**
     * 创建用例
     *
     * @param createCaseReq 创建请求
     * @return 返回操作记录的id
     */
    Long create(CreateCaseReq createCaseReq);

    /**
     * 获取用例数量
     *
     * @return 用例数量结果
     */
    List<CaseCountResp> count();

    /**
     * 删除用例
     *
     * @param id 用例id
     */
    void delete(Long id);

    /**
     * 批量创建用例
     *
     * @param createCaseReqList 创建请求集合
     */
    void batchCreate(List<CreateCaseReq> createCaseReqList);
}
