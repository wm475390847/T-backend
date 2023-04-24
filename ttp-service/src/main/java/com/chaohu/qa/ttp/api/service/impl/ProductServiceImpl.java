package com.chaohu.qa.ttp.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaohu.qa.ttp.api.enums.ErrorCodeEnum;
import com.chaohu.qa.ttp.api.exception.ServiceException;
import com.chaohu.qa.ttp.api.service.IProductService;
import com.chaohu.qa.ttp.api.util.TransferObjUtil;
import com.chaohu.qa.ttp.api.util.TransferPageUtil;
import com.chaohu.qa.ttp.api.vo.req.CreateProductReq;
import com.chaohu.qa.ttp.api.vo.resp.ProductResp;
import com.chaohu.qa.ttp.api.vo.resp.ServiceResp;
import com.chaohu.qa.ttp.db.config.CustomPage;
import com.chaohu.qa.ttp.db.dao.ProductInfoMapper;
import com.chaohu.qa.ttp.db.dao.ServiceInfoMapper;
import com.chaohu.qa.ttp.db.po.ProductInfo;
import com.chaohu.qa.ttp.db.po.ServiceInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wangmin
 * @date 2022/7/29 23:07
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ProductServiceImpl implements IProductService {

    @Resource
    private ServiceInfoMapper serviceInfoMapper;
    @Resource
    private ProductInfoMapper productInfoMapper;

    @Override
    public List<ServiceResp> groupList() {
        List<ServiceInfo> serviceInfoList = serviceInfoMapper.selectList(new QueryWrapper<>());

        List<ServiceResp> serviceRespList = new ArrayList<>();
        serviceInfoList.forEach(serviceInfo -> {
            Integer serviceId = serviceInfo.getId();

            QueryWrapper<ProductInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("service_id", serviceId);
            wrapper.eq("deleted", false);
            List<ProductInfo> productInfoList = productInfoMapper.selectList(wrapper);

            List<ProductResp> productRespList = productInfoList.stream()
                    .map(e -> TransferObjUtil.poToVo(e, new ProductResp()))
                    .collect(Collectors.toList());

            if (productRespList.size() != 0) {
                serviceRespList.add(
                        new ServiceResp()
                                .setId(serviceId)
                                .setProducts(productRespList)
                                .setServiceName(serviceInfo.getServiceName()));
            }
        });

        return serviceRespList;
    }

    @Override
    public CustomPage<ProductResp> list(int pageNo, int pageSize) {
        QueryWrapper<ProductInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("deleted", false);
        IPage<ProductInfo> productInfoPage = productInfoMapper.selectPage(new Page<>(pageNo, pageSize), wrapper);
        return TransferPageUtil.pageToResp(productInfoPage, new ProductResp());
    }

    @Override
    public void create(CreateProductReq createProductReq) {
        ProductInfo productInfo = TransferObjUtil.voToPo(createProductReq, new ProductInfo());
        int insert = productInfoMapper.insertSelective(productInfo);
        Optional.of(insert).filter(e -> e != 0).orElseThrow(() -> new ServiceException(ErrorCodeEnum.P_CREATE_FAIL));
    }

    @Override
    public void update(CreateProductReq createProductReq) {
        Optional.of(createProductReq).filter(e -> e.getId() != null)
                .orElseThrow(() -> new ServiceException(ErrorCodeEnum.P_ID_IS_NULL));
        ProductInfo productInfo = TransferObjUtil.voToPo(createProductReq, new ProductInfo());
        int update = productInfoMapper.updateByPrimaryKeySelective(productInfo);
        Optional.of(update).filter(e -> e != 0).orElseThrow(() -> new ServiceException(ErrorCodeEnum.P_UPDATE_FAIL));
    }

    @Override
    public void delete(Integer id) {
        ProductInfo productInfo = new ProductInfo().setId(id).setDeleted(true);
        int delete = productInfoMapper.updateByPrimaryKeySelective(productInfo);
        Optional.of(delete).filter(e -> e != 0).orElseThrow(() -> new ServiceException(ErrorCodeEnum.P_DELETE_FAIL));
    }
}
