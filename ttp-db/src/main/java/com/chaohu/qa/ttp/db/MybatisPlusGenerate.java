package com.chaohu.qa.ttp.db;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangmin
 */
@Slf4j
@Component
public class MybatisPlusGenerate {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    public void run(String businessName, String[] tableNames) {
        AutoGenerator mpg = new AutoGenerator();
        //1、全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir").replace("ttp-web", "ttp-db");
        //生成路径
        gc.setOutputDir(projectPath + "/src/main/java/");
        //设置作者
        gc.setAuthor("wangmin");
        gc.setOpen(false);
        //第二次生成会把第一次生成的覆盖
        gc.setFileOverride(true);
        //生成的service接口名字首字母是否为I，这样设置就没有
        gc.setServiceName("%sService");
        gc.setEntityName("%s");
        gc.setMapperName("%sMapper");
        //生成resultMap
        gc.setBaseResultMap(true);
        mpg.setGlobalConfig(gc);

        //2、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        // 这里切换成自己连接的数据库地址信息
        dsc.setUrl(url + "?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true");
        dsc.setDriverName(driverClassName);
        dsc.setUsername(username);
        dsc.setPassword(password);

        // 连接数据的类型
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        // 3、包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName("");
        pc.setParent("com.xhzy.qa.ttp.db");
        if (businessName == null) {
            pc.setEntity("po");
            pc.setMapper("dao");
        } else {
            pc.setEntity("po." + businessName);
            pc.setMapper("dao." + businessName);
        }
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 velocity
        String templatePath = "/templates/mapper.xml.vm";
        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                if (businessName == null) {
                    return projectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
                } else {
                    return projectPath + "/src/main/resources/mapper/" + businessName + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
                }
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 4、策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        //使用lombok
        //想要生成的表名，想要一下对多个表生成，用逗号分隔
        log.info("需要自动生成的表名: {}", (Object) tableNames);
        strategy.setInclude(tableNames);
        strategy.setEntityLombokModel(true);
        //表名前缀过滤
        mpg.setStrategy(strategy);

        TemplateConfig templateConfig = new TemplateConfig();

        //控制 不生成 controller
        templateConfig.setController("");
        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        //5、执行
        mpg.execute();
    }
}
