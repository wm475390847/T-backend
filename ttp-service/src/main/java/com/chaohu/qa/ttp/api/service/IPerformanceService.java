package com.chaohu.qa.ttp.api.service;

import com.chaohu.qa.ttp.api.vo.req.PerformanceCreateReq;
import com.chaohu.qa.ttp.api.vo.req.PerformanceUpdateReq;
import com.chaohu.qa.ttp.api.vo.resp.PerfReportHistoryResp;
import com.chaohu.qa.ttp.api.vo.resp.PerformanceInfoResp;
import com.chaohu.qa.ttp.db.config.CustomPage;

import java.util.List;

/**
 * 性能测试服务
 *
 * @author wangmin
 * @date 2023/3/30 16:40
 */
public interface IPerformanceService {

    /**
     * 性能测试列表
     *
     * @param pageNo          页码
     * @param pageSize        页大小
     * @param performanceName 性能测试名称
     * @return 结果
     */
    CustomPage<PerformanceInfoResp> list(int pageNo, int pageSize, String performanceName);

    /**
     * 开始执行
     * <p>
     * 异步执行方法
     *
     * @param id 性能测试任务id
     */
    void start(Integer id);

    /**
     * 停止执行
     *
     * @param id 性能测试任务id
     */
    void stop(Integer id);

    /**
     * 创建性能测试
     *
     * @param createReq 创建数据
     */
    void create(PerformanceCreateReq createReq);

    /**
     * 更新性能测试
     *
     * @param updateReq 修改数据
     */
    void update(PerformanceUpdateReq updateReq);

    /**
     * 由于测试环境oss的文件每个月清理一次，所以需要定时批量更新jmx文件
     */
    void batchUpdate();

    /**
     * 删除性能测试
     *
     * @param id 性能测试任务id
     */
    void delete(Integer id);

    /**
     * 运行报告列表
     *
     * @param perfId 性能测试id
     * @return 结果
     */
    List<PerfReportHistoryResp> reports(Integer perfId);
}
