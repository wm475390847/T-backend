package com.chaohu.qa.ttp.db.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.chaohu.qa.ttp.db.MyBatisBaseMapper;
import com.chaohu.qa.ttp.db.po.CaseCountInfo;
import com.chaohu.qa.ttp.db.po.CaseInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * CaseInfoMapper继承基类
 *
 * @author wangmin
 */
public interface CaseInfoMapper extends MyBatisBaseMapper<CaseInfo, Long> {

    /**
     * 通过id结合查询用例信息
     *
     * @param caseIds 用例id集合
     * @return 用例集合
     */
    @Select("<script>" +
            "select * from case_info where id in " +
            "<foreach item='item' index='index' collection='caseIds' open='(' separator=', ' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</script>")
    List<CaseInfo> selectListByCaseIds(@Param("caseIds") List<Long> caseIds);

    /**
     * 查询用例数量
     *
     * @return CaseCount
     */
    @Select("<script>" +
            "select product_id, product_name, env, count(*)\n" +
            "from case_info as c\n" +
            "         left join product_info as p on c.product_id = p.id\n" +
            "group by c.product_id, c.env;" +
            "</script>")
    @Results(
            {
                    @Result(property = "productId", column = "product_id"),
                    @Result(property = "productName", column = "product_name"),
                    @Result(property = "count", column = "count(*)"),
                    @Result(property = "env", column = "env")
            })
    List<CaseCountInfo> selectCaseCount();

    /**
     * 查询用例列表
     *
     * @param page         分页
     * @param queryWrapper 查询条件
     * @return 用例集合
     */
    @Select("<script>" +
            "select c.*,p.product_name " +
            "from case_info AS c LEFT JOIN product_info AS p ON c.product_id = p.id " +
            "<if test=\"ew!=null\"> ${ew.customSqlSegment} </if>" +
            "order by IF(case_result = 0, 0, 1), gmt_create desc" +
            "</script>")
    IPage<CaseInfo> selectPage(IPage<CaseInfo> page, @Param(Constants.WRAPPER) QueryWrapper<CaseInfo> queryWrapper);
}