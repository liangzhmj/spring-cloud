package com.liangzhmj.cat.api.service.annotation;


import com.liangzhmj.cat.api.service.config.SynaServletConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SynaServletConfig.class})
public @interface EnableSynaServlet {
}
