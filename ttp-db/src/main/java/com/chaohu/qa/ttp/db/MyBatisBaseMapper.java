package com.chaohu.qa.ttp.db;

import java.io.Serializable;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * DAO公共基类，由MybatisGenerator自动生成请勿修改
 *
 * @param <Model> The Model Class 这里是泛型不是Model类
 * @param <PK>    The Primary Key Class 如果是无主键，则可以用Model来跳过，如果是多主键则是Key类
 * @author wangmin
 */
public interface MyBatisBaseMapper<Model, PK extends Serializable> extends BaseMapper<Model> {
    /**
     * 通过主键删除
     *
     * @param id 主键
     * @return 结果
     */
    int deleteByPrimaryKey(PK id);

    /**
     * 插入
     *
     * @param record 记录
     * @return 结果
     */
    @Override
    int insert(Model record);

    /**
     * 选择性插入
     *
     * @param record 记录
     * @return 结果
     */
    int insertSelective(Model record);

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 结果
     */
    Model selectByPrimaryKey(PK id);

    /**
     * 通过主键选择性修改
     *
     * @param record 记录
     * @return 结果
     */
    int updateByPrimaryKeySelective(Model record);

    /**
     * 通过主键修改
     *
     * @param record 记录
     * @return 结果
     */
    int updateByPrimaryKey(Model record);

    /**
     * 分页查询
     *
     * @param pageNumber   页码
     * @param pageSize     页大小
     * @param queryWrapper 查询条件
     * @return 列表
     */
    default IPage<Model> selectPage(int pageNumber, int pageSize, Wrapper<Model> queryWrapper) {
        IPage<Model> page = new Page<>(pageNumber, pageSize);
        return selectPage(page, queryWrapper);
    }
}