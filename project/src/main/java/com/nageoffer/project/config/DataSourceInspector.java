package com.nageoffer.project.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;


/**
 * 检测由多少个数据库连接池
 */
@Configuration
public class DataSourceInspector {

    @Autowired
    private ApplicationContext ctx;

    @PostConstruct
    public void inspect() {
        // 获取所有 DataSource 类型的 Bean 名称
        String[] names = ctx.getBeanNamesForType(javax.sql.DataSource.class);
        System.out.println("Total DataSource beans: " + names.length);
    }
}
