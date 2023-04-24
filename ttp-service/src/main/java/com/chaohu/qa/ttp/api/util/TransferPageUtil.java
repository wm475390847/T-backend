package com.chaohu.qa.ttp.api.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.chaohu.qa.ttp.db.config.CustomPage;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 页码转换工具
 *
 * @author wangmin
 * @date 2022/4/22 11:34
 */
@Slf4j
public class TransferPageUtil {

    public static <P, V> CustomPage<V> pageToResp(IPage<P> oldPage, V clazz) {

        List<V> list = oldPage.getRecords().stream().map(e ->
                TransferObjUtil.poToVo(e, clazz)).collect(Collectors.toList());
        log.info("page转换结果: {}", list);

        return new CustomPage<V>()
                .setPages(oldPage.getPages())
                .setTotal(oldPage.getTotal())
                .setCurrent(oldPage.getCurrent())
                .setRecords(list);
    }

    public static <P, V> CustomPage<V> pageToResp(IPage<P> oldPage, List<V> records) {
        return new CustomPage<V>()
                .setPages(oldPage.getPages())
                .setTotal(oldPage.getTotal())
                .setCurrent(oldPage.getCurrent())
                .setRecords(records);
    }
}
