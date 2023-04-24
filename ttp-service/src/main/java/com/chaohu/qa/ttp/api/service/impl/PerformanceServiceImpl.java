package com.chaohu.qa.ttp.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaohu.qa.ttp.api.dto.RequestDataDTO;
import com.chaohu.qa.ttp.api.dto.ThreadDataDTO;
import com.chaohu.qa.ttp.api.enums.ErrorCodeEnum;
import com.chaohu.qa.ttp.api.exception.ServiceException;
import com.chaohu.qa.ttp.api.manager.JmeterManager;
import com.chaohu.qa.ttp.api.manager.OssManager;
import com.chaohu.qa.ttp.api.service.IPerformanceService;
import com.chaohu.qa.ttp.api.util.CommonUtil;
import com.chaohu.qa.ttp.api.util.TransferPageUtil;
import com.chaohu.qa.ttp.api.vo.req.PerformanceCreateReq;
import com.chaohu.qa.ttp.api.vo.req.PerformanceUpdateReq;
import com.chaohu.qa.ttp.api.vo.resp.PerfReportHistoryResp;
import com.chaohu.qa.ttp.api.vo.resp.PerformanceInfoResp;
import com.chaohu.qa.ttp.db.config.CustomPage;
import com.chaohu.qa.ttp.db.dao.PerfReportHistoryMapper;
import com.chaohu.qa.ttp.db.dao.PerformanceInfoMapper;
import com.chaohu.qa.ttp.db.po.PerfReportHistory;
import com.chaohu.qa.ttp.db.po.PerformanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.HashTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author wangmin
 * @date 2023/3/30 16:42
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PerformanceServiceImpl implements IPerformanceService {

    @Resource
    private PerfReportHistoryMapper perfReportHistoryMapper;
    @Resource
    private PerformanceInfoMapper performanceInfoMapper;
    @Resource
    private JmeterManager jmeterManager;
    @Resource
    private OssManager ossManager;
    @Value("${oss.jmx.folder}")
    private String ossJmxFolder;
    @Value("${oss.report.folder}")
    private String ossReportFolder;

    private final String jmeterFileFolder = System.getProperty("user.dir") + "/file/";

    private static final Map<Integer, StandardJMeterEngine> ENGINE_MAP = new ConcurrentHashMap<>();

    private final ExecutorService executePool = new ThreadPoolExecutor(
            3, 3, 3,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(3),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    @Override
    public CustomPage<PerformanceInfoResp> list(int pageNo, int pageSize, String performanceName) {
        QueryWrapper<PerformanceInfo> wrapper = new QueryWrapper<>();
        if (performanceName != null) {
            wrapper.like("performance_name", performanceName);
        }
        wrapper.orderByDesc("gmt_create");
        IPage<PerformanceInfo> performanceInfoPage = performanceInfoMapper.selectPage(pageNo, pageSize, wrapper);
        return TransferPageUtil.pageToResp(performanceInfoPage, new PerformanceInfoResp());
    }

    @Override
    public void start(Integer id) {
        PerformanceInfo performanceInfo = performanceInfoMapper.selectByPrimaryKey(id);
        // 判断数据不能为空
        Optional.ofNullable(performanceInfo).filter(e -> e.getJmxPath() != null)
                .orElseThrow(() -> new ServiceException(ErrorCodeEnum.PERF_IS_NULL));
        Optional.of(performanceInfo).filter(e -> e.getStatus() != 2)
                .orElseThrow(() -> new ServiceException("当前任务正在运行中"));
        // todo 后续可能需要动态判断cpu的数据来决定是否执行
        Optional.of(ENGINE_MAP).filter(Map::isEmpty)
                .orElseThrow(() -> new ServiceException("已存在运行中的压测任务"));
        // 执行记录入库
        PerfReportHistory perfReportHistory = new PerfReportHistory().setPerfId(id);
        int startInsertHistory = perfReportHistoryMapper.insertSelective(perfReportHistory);
        Optional.of(startInsertHistory).filter(e -> e != 0)
                .orElseThrow(() -> new ServiceException(ErrorCodeEnum.HISTORY_CREATE_FAIL));

        // 开始执行时记录当前时间戳，修改状态为运行中、执行时间为当前时间
        long startTime = System.currentTimeMillis();
        performanceInfo.setStatus(2).setExecuteTime(new Date());
        int startUpdatePerf = performanceInfoMapper.updateByPrimaryKeySelective(performanceInfo);
        Optional.of(startUpdatePerf).filter(e -> e != 0)
                .orElseThrow(() -> new ServiceException(ErrorCodeEnum.P_UPDATE_FAIL));
        File file = null;
        try {
            String fileName = CommonUtil.getFileName(performanceInfo.getJmxPath(), "jmx_file");
            file = ossManager.download(ossJmxFolder + fileName + ".jmx",
                    jmeterFileFolder + fileName + ".jmx");
            StandardJMeterEngine engine = jmeterManager.getJmeterEngine(file);
            // 将运行的引擎放入缓存中
            ENGINE_MAP.put(id, engine);
            executePool.execute(() -> {
                log.info("====>>>>开始运行<<<<====");
                engine.run();
                log.info("====>>>>运行结束<<<<====");
                // 运行结束时更新性能测试状态
                long elapsedTime = System.currentTimeMillis() - startTime;
                performanceInfo.setStatus(3).setElapsedTime(elapsedTime);
                int finishUpdatePerf = performanceInfoMapper.updateByPrimaryKeySelective(performanceInfo);
                Optional.of(finishUpdatePerf).filter(e -> e != 0)
                        .orElseThrow(() -> new ServiceException(ErrorCodeEnum.P_UPDATE_FAIL));
                log.info("====>>>>生成报告<<<<====");
                // 生成文件夹名称，便于找到结果报告在oss上面的位置
                String fileFolder = id + "-" + perfReportHistory.getId();
                jmeterManager.generatorReport(fileFolder);
                log.info("====>>>>生成结束<<<<====");
                // 删除结果文件
                CommonUtil.deleteFolder(new File(jmeterFileFolder + "result.jtl"));
                // 将报告上传至oss并删除本地报告
                File reportFile = new File(jmeterFileFolder + fileFolder);
                ossManager.uploadFolder(reportFile, ossReportFolder + fileFolder + "/");
                CommonUtil.deleteFolder(reportFile);
                // 主页面路径
                String reportPath = ossManager.getBashOssPath() + ossReportFolder + fileFolder + "/" + "index.html";
                perfReportHistory.setReportPath(reportPath).setElapsedTime(elapsedTime);
                // 运行结束时更新记录表
                int finishUpdateHistory = perfReportHistoryMapper.updateByPrimaryKeySelective(perfReportHistory);
                Optional.of(finishUpdateHistory).filter(e -> e != 0)
                        .orElseThrow(() -> new ServiceException(ErrorCodeEnum.FILE_UPLOAD_FAIL));
            });

        } catch (Exception e) {
            log.error("执行错误: {}", e.getMessage());
            e.printStackTrace();
            throw new ServiceException(ErrorCodeEnum.PREF_START_FAIL);
        } finally {
            ENGINE_MAP.remove(id);
            CommonUtil.deleteFolder(file);
        }
    }

    @Override
    public void stop(Integer id) {
        StandardJMeterEngine engine = ENGINE_MAP.get(id);
        engine.stopTest();
        ENGINE_MAP.remove(id);
        // 数据库转改也需要改掉
        PerformanceInfo performanceInfo = new PerformanceInfo().setStatus(3).setId(id);
        int update = performanceInfoMapper.updateByPrimaryKeySelective(performanceInfo);
        Optional.of(update).filter(e -> e != 0).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PERF_STOP_FAIL));
    }

    @Override
    public void create(PerformanceCreateReq createReq) {
        String ossPath = createJmxAndUploadToOss(createReq.getRequestData(), createReq.getThreadGroupData(), null);
        // 数据入库
        JSONObject requestData = (JSONObject) JSON.toJSON(createReq.getRequestData());
        JSONObject threadData = (JSONObject) JSON.toJSON(createReq.getThreadGroupData());
        PerformanceInfo performanceInfo = new PerformanceInfo()
                .setRequestData(requestData)
                .setThreadData(threadData)
                .setPerformanceName(createReq.getPerformanceName())
                .setJmxPath(ossPath)
                .setExecuteTime(null);
        int insert = performanceInfoMapper.insertSelective(performanceInfo);
        Optional.of(insert).filter(e -> insert != 0)
                .orElseThrow(() -> new ServiceException(ErrorCodeEnum.PERF_CREATE_FAIL));
    }

    @Override
    public void update(PerformanceUpdateReq updateReq) {
        PerformanceInfo performanceInfo = performanceInfoMapper.selectById(updateReq.getId());
        Optional.ofNullable(performanceInfo).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PERF_IS_NULL));
        String ossPath = createJmxAndUploadToOss(updateReq.getRequestData(), updateReq.getThreadData(), performanceInfo);
        // 数据转换
        JSONObject requestData = (JSONObject) JSONObject.toJSON(updateReq.getRequestData());
        JSONObject threadData = (JSONObject) JSONObject.toJSON(updateReq.getThreadData());
        // 更新实体入库
        performanceInfo.setPerformanceName(updateReq.getPerformanceName())
                .setRequestData(requestData)
                .setThreadData(threadData)
                .setJmxPath(ossPath);
        int update = performanceInfoMapper.updateByPrimaryKeySelective(performanceInfo);
        Optional.of(update).filter(e -> update != 0)
                .orElseThrow(() -> new ServiceException(ErrorCodeEnum.PERF_UPDATE_FAIL));
    }

    @Override
    public void batchUpdate() {
        QueryWrapper<PerformanceInfo> wrapper = new QueryWrapper<>();
        List<PerformanceInfo> performanceInfos = performanceInfoMapper.selectList(wrapper);
        CopyOnWriteArrayList<PerformanceInfo> copyOnWriteArrayList = new CopyOnWriteArrayList<>(performanceInfos);
        executePool.execute(() -> copyOnWriteArrayList.stream().filter(Objects::nonNull).forEach(e -> {
            ThreadDataDTO threadDataDTO = JSONObject.toJavaObject(e.getThreadData(), ThreadDataDTO.class);
            RequestDataDTO requestDataDTO = JSONObject.toJavaObject(e.getRequestData(), RequestDataDTO.class);
            String ossPath = createJmxAndUploadToOss(requestDataDTO, threadDataDTO, e);
            // 更新实体入库
            e.setJmxPath(ossPath);
            int update = performanceInfoMapper.updateByPrimaryKeySelective(e);
            Optional.of(update).filter(o -> update != 0)
                    .orElseThrow(() -> new ServiceException(ErrorCodeEnum.PERF_UPDATE_FAIL));
        }));
    }

    @Override
    public void delete(Integer id) {
        PerformanceInfo performanceInfo = performanceInfoMapper.selectById(id);
        Optional.ofNullable(performanceInfo).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PERF_IS_NULL));
        performanceInfo.setDeleted(true);
        int update = performanceInfoMapper.updateByPrimaryKeySelective(performanceInfo);
        Optional.of(update).filter(e -> e != 0).orElseThrow(() -> new ServiceException(ErrorCodeEnum.PERF_DELETE_FAIL));
    }

    @Override
    public List<PerfReportHistoryResp> reports(Integer perfId) {
        QueryWrapper<PerfReportHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("perf_id", perfId);
        wrapper.orderByDesc("gmt_create");
        List<PerfReportHistory> perfReportHistories = perfReportHistoryMapper.selectList(wrapper);
        List<PerfReportHistoryResp> arrayList = new ArrayList<>();
        perfReportHistories.forEach(e -> {
            PerfReportHistoryResp perfReportHistoryResp = new PerfReportHistoryResp();
            perfReportHistoryResp.setId(e.getId());
            perfReportHistoryResp.setPerfId(e.getPerfId());
            perfReportHistoryResp.setReportPath(ossManager.addSign(e.getReportPath()));
            perfReportHistoryResp.setElapsedTime(e.getElapsedTime());
            perfReportHistoryResp.setGmtModified(e.getGmtModified());
            perfReportHistoryResp.setGmtCreate(e.getGmtCreate());
            arrayList.add(perfReportHistoryResp);
        });
        return arrayList;
    }

    /**
     * 创建jmx文件并上传至oss
     *
     * @param requestDataDTO  请求数据
     * @param threadDataDTO   线程数据
     * @param performanceInfo 性能数据
     * @return 文件上传的位置
     */
    private String createJmxAndUploadToOss(RequestDataDTO requestDataDTO, ThreadDataDTO threadDataDTO, PerformanceInfo performanceInfo) {
        File file = null;
        try {
            // 如果存在性能测试就更新源文件，如果不存在就保存为新的
            String fileName = performanceInfo == null ? UUID.randomUUID().toString() :
                    CommonUtil.getFileName(performanceInfo.getJmxPath(), "jmx_file");
            // 需要生成jxm文件
            HashTree testPlanTree = jmeterManager.createTestPlanTree(requestDataDTO, threadDataDTO);
            // 指定输入文件地址
            String filePath = jmeterFileFolder + fileName + ".jmx";
            SaveService.saveTree(testPlanTree, new FileOutputStream(filePath));
            // 指定上传至oss的位置
            file = new File(filePath);
            return ossManager.upload(file, ossJmxFolder);
        } catch (Exception e) {
            log.error("文件生成错误: {}", e.getMessage());
            e.printStackTrace();
        } finally {
            CommonUtil.deleteFolder(file);
        }
        return null;
    }
}
