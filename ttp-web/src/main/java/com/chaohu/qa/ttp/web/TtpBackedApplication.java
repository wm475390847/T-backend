package com.chaohu.qa.ttp.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * @author wangmin
 */

@ServletComponentScan
@SpringBootApplication
@ComponentScan(basePackages = "com.chaohu.qa.ttp.*")
@MapperScan(basePackages = "com.xhzy.qa.ttp.db")
@PropertySource("classpath:env.properties")
public class TtpBackedApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }

    public static void main(String[] args) {
        SpringApplication.run(TtpBackedApplication.class, args);
    }

}
