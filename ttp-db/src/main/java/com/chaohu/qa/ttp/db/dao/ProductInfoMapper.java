package com.chaohu.qa.ttp.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.chaohu.qa.ttp.db.MyBatisBaseMapper;
import com.chaohu.qa.ttp.db.po.ProductInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * ProductInfoMapper继承基类
 *
 * @author wangmin
 */
public interface ProductInfoMapper extends MyBatisBaseMapper<ProductInfo, Integer> {

    /**
     * 查询产品页
     *
     * @param page         页码
     * @param queryWrapper 查询条件
     * @return 产品列表
     */
    @Select("<script>" +
            "SELECT a.*,s.service_name FROM product_info AS a LEFT JOIN service_info AS s ON a.service_id = s.id " +
            "<if test=\"ew!=null\"> ${ew.customSqlSegment} </if>" +
            "</script>")
    IPage<ProductInfo> selectPage(IPage<ProductInfo> page, @Param(Constants.WRAPPER) QueryWrapper<ProductInfo> queryWrapper);
}