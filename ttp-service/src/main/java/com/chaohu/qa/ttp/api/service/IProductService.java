package com.chaohu.qa.ttp.api.service;

import com.chaohu.qa.ttp.api.vo.req.CreateProductReq;
import com.chaohu.qa.ttp.api.vo.resp.ProductResp;
import com.chaohu.qa.ttp.api.vo.resp.ServiceResp;
import com.chaohu.qa.ttp.db.config.CustomPage;

import java.util.List;

/**
 * @author wangmin
 * @date 2022/7/29 23:06
 */
public interface IProductService {

    /**
     * 产品分组列表
     *
     * @return list
     */
    List<ServiceResp> groupList();

    /**
     * 产品列表
     *
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 列表
     */
    CustomPage<ProductResp> list(int pageNo, int pageSize);

    /**
     * 创建产品线
     *
     * @param createProductReq 创建参数
     */
    void create(CreateProductReq createProductReq);

    /**
     * 删除产品
     *
     * @param id 产品id
     */
    void delete(Integer id);

    /**
     * 修改产品
     *
     * @param createProductReq 修改请求
     */
    void update(CreateProductReq createProductReq);
}
