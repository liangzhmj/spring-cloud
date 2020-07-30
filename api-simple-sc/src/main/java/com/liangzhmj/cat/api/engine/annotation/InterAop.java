package com.liangzhmj.cat.api.engine.annotation;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InterAop {
    int value() default 0;
}
