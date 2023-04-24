package com.chaohu.qa.ttp.web.controller;

import com.chaohu.qa.ttp.api.service.ICaseInfoService;
import com.chaohu.qa.ttp.api.vo.req.CaseListReq;
import com.chaohu.qa.ttp.api.vo.req.CreateCaseReq;
import com.chaohu.qa.ttp.api.vo.resp.CaseCountResp;
import com.chaohu.qa.ttp.api.vo.resp.CaseInfoResp;
import com.chaohu.qa.ttp.db.config.CustomPage;
import com.chaohu.qa.ttp.web.Response.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangmin
 * @date 2022/7/15 11:28
 */
@RequestMapping("/ttp/cases")
@RestController
public class CaseController {

    @Resource
    private ICaseInfoService caseInfoService;

    @GetMapping()
    public Result<Object> list(@RequestParam("pageNo") int pageNo,
                               @RequestParam("pageSize") int pageSize,
                               @RequestParam(value = "caseResult", required = false) Boolean caseResult,
                               @RequestParam(value = "productId", required = false) Integer productId,
                               @RequestParam(value = "env", required = false) String env,
                               @RequestParam(value = "caseOwner", required = false) String caseOwner,
                               @RequestParam(value = "caseName", required = false) String caseName) {
        CustomPage<CaseInfoResp> list = caseInfoService.list(pageNo, pageSize, caseResult, productId, env, caseOwner, caseName);
        return Result.success(list);
    }

    @PostMapping()
    public Result<Object> list(@RequestBody CaseListReq caseListReq) {
        List<CaseInfoResp> list = caseListReq.getProductId() == null ? caseInfoService.list(caseListReq.getCaseIds()) : caseInfoService.list(caseListReq.getProductId());
        return Result.success(list);
    }

    @GetMapping("/count")
    public Result<Object> count() {
        List<CaseCountResp> caseCountRespList = caseInfoService.count();
        return Result.success(caseCountRespList);
    }

    @PostMapping("/create")
    public Result<Object> create(@RequestBody CreateCaseReq createCaseReq) {
        Long caseId = caseInfoService.create(createCaseReq);
        return Result.success(caseId);
    }

    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable(value = "id") Long id) {
        caseInfoService.delete(id);
        return Result.success("删除成功");
    }

    @PostMapping("/batchCreate")
    public Result<Object> create(@RequestBody List<CreateCaseReq> createCaseReqList) {
        caseInfoService.batchCreate(createCaseReqList);
        return Result.success("创建成功");
    }
}
