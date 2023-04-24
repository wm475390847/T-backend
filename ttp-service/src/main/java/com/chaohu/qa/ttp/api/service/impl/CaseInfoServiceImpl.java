package com.chaohu.qa.ttp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaohu.qa.ttp.api.enums.ErrorCodeEnum;
import com.chaohu.qa.ttp.api.exception.ServiceException;
import com.chaohu.qa.ttp.api.service.ICaseInfoService;
import com.chaohu.qa.ttp.api.util.TransferObjUtil;
import com.chaohu.qa.ttp.api.util.TransferPageUtil;
import com.chaohu.qa.ttp.api.vo.req.CreateCaseReq;
import com.chaohu.qa.ttp.api.vo.resp.CaseCountResp;
import com.chaohu.qa.ttp.api.vo.resp.CaseInfoResp;
import com.chaohu.qa.ttp.db.config.CustomPage;
import com.chaohu.qa.ttp.db.dao.CaseInfoMapper;
import com.chaohu.qa.ttp.db.po.CaseCountInfo;
import com.chaohu.qa.ttp.db.po.CaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author wangmin
 * @date 2022/6/23 10:55
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class CaseInfoServiceImpl implements ICaseInfoService {

    private final ExecutorService createPool = new ThreadPoolExecutor(
            3, 3, 3,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(3),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    @Resource
    private CaseInfoMapper caseInfoMapper;

    @Override
    public CustomPage<CaseInfoResp> list(int pageNo, int pageSize, Boolean caseResult,
                                         Integer productId, String env, String caseOwner, String caseName) {
        QueryWrapper<CaseInfo> wrapper = new QueryWrapper<>();
        if (caseResult != null) {
            wrapper.eq("case_result", caseResult);
        }
        if (productId != null) {
            wrapper.eq("product_id", productId);
        }
        if (StringUtils.isNotBlank(env)) {
            wrapper.eq("env", env);
        }
        if (StringUtils.isNotBlank(caseOwner)) {
            wrapper.like("case_owner", caseOwner);
        }
        if (StringUtils.isNotEmpty(caseName)) {
            wrapper.like("case_name", caseName);
        }
        IPage<CaseInfo> caseInfoPage = caseInfoMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return TransferPageUtil.pageToResp(caseInfoPage, new CaseInfoResp());
    }

    @Override
    public List<CaseInfoResp> list(Integer productId) {
        QueryWrapper<CaseInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("product_id", productId);

        List<CaseInfo> caseInfos = caseInfoMapper.selectList(wrapper);
        return caseInfos.stream().map(e -> TransferObjUtil.poToVo(e, new CaseInfoResp())).collect(Collectors.toList());
    }

    @Override
    public List<CaseInfoResp> list(List<Long> caseIds) {
        List<CaseInfo> caseInfos = caseInfoMapper.selectListByCaseIds(caseIds);
        return caseInfos.stream().map(e -> TransferObjUtil.poToVo(e, new CaseInfoResp())).collect(Collectors.toList());
    }

    @Override
    public Long create(CreateCaseReq createCaseReq) {
        CaseInfo caseInfo = TransferObjUtil.voToPo(createCaseReq, new CaseInfo());
        log.info("[caseInfo]: {}", caseInfo);
        QueryWrapper<CaseInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("case_name", caseInfo.getCaseName());
        wrapper.eq("env", caseInfo.getEnv());
        CaseInfo selectCaseInfo = caseInfoMapper.selectOne(wrapper);
        if (selectCaseInfo == null) {
            int insert = caseInfoMapper.insertSelective(caseInfo);
            Optional.of(insert).filter(e -> e != 0).orElseThrow(() -> new ServiceException(ErrorCodeEnum.C_CREATE_FAIL));
        } else {
            caseInfo.setId(selectCaseInfo.getId())
                    .setExecuteTime(new Date())
                    .setGmtModified(new Date());
            int update = caseInfoMapper.updateByPrimaryKeySelective(caseInfo);
            Optional.of(update).filter(e -> e != 0).orElseThrow(() -> new ServiceException(ErrorCodeEnum.C_CREATE_FAIL));
        }
        return caseInfo.getId();
    }

    @Override
    public List<CaseCountResp> count() {
        List<CaseCountInfo> caseCountInfoList = caseInfoMapper.selectCaseCount();
        log.info("caseInfoCountList: {}", caseCountInfoList);
        List<CaseCountInfo> testCaseCountInfoList = caseCountInfoList.stream().filter(e -> "test".equals(e.getEnv())).collect(Collectors.toList());
        caseCountInfoList.removeAll(testCaseCountInfoList);
        List<CaseCountInfo> caseCountInfos = caseCountInfoList.stream().filter(e -> "prod".equals(e.getEnv())).collect(Collectors.toList());
        log.info("{}", caseCountInfos);
        List<CaseCountResp> caseCountRespList = new ArrayList<>();
        CaseCountResp testCaseCountResp = new CaseCountResp().setEnv("test").setCaseCountList(testCaseCountInfoList);
        CaseCountResp caseCountResp = new CaseCountResp().setEnv("prod").setCaseCountList(caseCountInfoList);
        caseCountRespList.add(testCaseCountResp);
        caseCountRespList.add(caseCountResp);
        return caseCountRespList;
    }

    @Override
    public void delete(Long id) {
        CaseInfo caseInfo = caseInfoMapper.selectByPrimaryKey(id);
        if (caseInfo == null) {
            throw new ServiceException(ErrorCodeEnum.C_NOT_FOUND);
        }
        int delete = caseInfoMapper.deleteByPrimaryKey(id);
        Optional.of(delete).filter(e -> e != 0).orElseThrow(() -> new ServiceException(ErrorCodeEnum.C_DELETE_FAIL));
    }

    @Override
    public void batchCreate(List<CreateCaseReq> createCaseReqList) {
        createPool.execute(() -> createCaseReqList.forEach(this::create));
    }
}
