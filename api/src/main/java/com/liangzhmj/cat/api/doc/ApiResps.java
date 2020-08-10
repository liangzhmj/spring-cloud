package com.liangzhmj.cat.api.doc;


import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiResps {
    ApiResp[] value();
}
