package com.liangzhmj.cat.dao.cache.annotation;


import com.liangzhmj.cat.dao.cache.EhcacheContext;
import com.liangzhmj.cat.dao.cache.config.CacheRegister;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableCaching
@Import({EhcacheContext.class, CacheRegister.class})
public @interface EnableEhcache {

}
