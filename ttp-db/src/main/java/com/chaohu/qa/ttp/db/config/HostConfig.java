package com.chaohu.qa.ttp.db.config;

import io.leopard.javahost.JavaHost;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Java虚拟机启动，在pod内部添加host
 *
 * @author wangmin
 * @date 2023/4/11 15:03
 */
@Configuration
public class HostConfig {

    private static final Properties PROPS = new Properties();

    static {
        PROPS.put("test-yuanmao.shuwen.com", "118.31.181.18");
        JavaHost.updateVirtualDns(PROPS);
        JavaHost.printAllVirtualDns();
    }
}
