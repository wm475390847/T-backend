package com.chaohu.qa.ttp.api.util;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 对象转换工具
 *
 * @author wangmin
 * @date 2022/4/22 11:34
 */
@Slf4j
public class TransferObjUtil {

    public static <V, P> P voToPo(V vo, P po) {
        copySameProperties(po, vo);
        return po;
    }

    public static <V, P> V poToVo(P po, V vo) {
        try {
            vo = (V) vo.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        copySameProperties(vo, po);
        return vo;
    }

    public static <D, P> D poToDto(P po, D dto) {
        return poToVo(po, dto);
    }

    /**
     * 复制数据
     *
     * @param target 目标数据
     * @param source 元数据
     */
    private static void copySameProperties(Object source, Object target) {
        BeanUtil.copyProperties(target, source);
    }
}
