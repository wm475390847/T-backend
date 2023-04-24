package com.chaohu.qa.ttp.web.controller;

import com.chaohu.qa.ttp.api.exception.ServiceException;
import com.chaohu.qa.ttp.api.service.IPerformanceService;
import com.chaohu.qa.ttp.api.vo.req.PerformanceCreateReq;
import com.chaohu.qa.ttp.api.vo.req.PerformanceUpdateReq;
import com.chaohu.qa.ttp.api.vo.resp.PerfReportHistoryResp;
import com.chaohu.qa.ttp.api.vo.resp.PerformanceInfoResp;
import com.chaohu.qa.ttp.db.config.CustomPage;
import com.chaohu.qa.ttp.web.Response.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author wangmin
 * @date 2023/4/7 16:54
 */
@RequestMapping("/ttp/perf")
@RestController()
public class PerformanceController {

    @Resource
    private IPerformanceService iPerformanceService;

    @GetMapping()
    public Result<Object> list(@RequestParam("pageNo") int pageNo,
                               @RequestParam("pageSize") int pageSize,
                               @RequestParam(value = "performanceName", required = false) String performanceName) {
        CustomPage<PerformanceInfoResp> list = iPerformanceService.list(pageNo, pageSize, performanceName);
        return Result.success(list);
    }

    @PostMapping("/start/{id}")
    public Result<Object> start(@PathVariable("id") Integer id) {
        Optional.ofNullable(id).orElseThrow(() -> new ServiceException("性能测试id不能为空"));
        iPerformanceService.start(id);
        return Result.success("开始执行");
    }

    @PostMapping("/stop/{id}")
    public Result<Object> stop(@PathVariable("id") Integer id) {
        Optional.ofNullable(id).orElseThrow(() -> new ServiceException("性能测试id不能为空"));
        iPerformanceService.stop(id);
        return Result.success("停止成功");
    }

    @PostMapping()
    public Result<Object> create(@RequestBody PerformanceCreateReq performanceCreateReq) {
        iPerformanceService.create(performanceCreateReq);
        return Result.success("创建成功");
    }

    @PutMapping()
    public Result<Object> update(@RequestBody PerformanceUpdateReq performanceUpdateReq) {
        iPerformanceService.update(performanceUpdateReq);
        return Result.success("修改成功");
    }

    @PutMapping("/batchUpdate")
    public Result<Object> batchUpdate() {
        iPerformanceService.batchUpdate();
        return Result.success("批量修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        iPerformanceService.delete(id);
        return Result.success("删除成功");
    }

    @GetMapping("/reports/{perfId}")
    public Result<Object> reports(@PathVariable("perfId") Integer perfId) {
        List<PerfReportHistoryResp> reports = iPerformanceService.reports(perfId);
        return Result.success(reports);
    }
}
