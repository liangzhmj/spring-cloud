package com.liangzhmj.cat.dao.redis.annotation;


import com.liangzhmj.cat.dao.redis.config.RedisConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RedisConfig.class)
public @interface EnableRedisDao {
}
