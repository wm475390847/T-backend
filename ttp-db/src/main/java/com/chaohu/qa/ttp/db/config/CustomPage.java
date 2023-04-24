package com.chaohu.qa.ttp.db.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 自定义page分页
 *
 * @author wangmin
 * @date 2022/4/24 12:46
 */
@Data
@Accessors(chain = true)
public class CustomPage<T> {

    private Long total;
    private Long pages;
    private Long current;
    private List<T> records;

}
