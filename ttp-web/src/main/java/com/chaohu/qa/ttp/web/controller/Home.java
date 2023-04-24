package com.chaohu.qa.ttp.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangmin
 * @date 2022/7/15 11:21
 */
@RestController
public class Home {

    @RequestMapping("/ok")
    public Object webStart() {
        return "Hello Word";
    }
}
