package com.liangzhmj.cat.dao.mysql.annotation;


import com.liangzhmj.cat.dao.mysql.config.DataSourceService;
import com.liangzhmj.cat.dao.mysql.config.DruidMonitor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DataSourceService.class, DruidMonitor.class})
public @interface EnableServiceDao {
}
