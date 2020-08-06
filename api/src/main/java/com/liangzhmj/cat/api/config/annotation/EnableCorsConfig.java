package com.liangzhmj.cat.api.config.annotation;


import com.liangzhmj.cat.api.config.CorsConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CorsConfig.class})
public @interface EnableCorsConfig {
}
