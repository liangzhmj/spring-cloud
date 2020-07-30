package com.liangzhmj.cat.dao.dbconfig.annotation;


import com.liangzhmj.cat.dao.dbconfig.DBProperties;
import com.liangzhmj.cat.dao.dbconfig.config.DBConfig;
import com.liangzhmj.cat.dao.dbconfig.config.DBConfigRegister;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DBConfig.class, DBProperties.class, DBConfigRegister.class})
public @interface EnableDBConfig {
}
