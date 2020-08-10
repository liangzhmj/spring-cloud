package com.liangzhmj.cat.api.doc;


import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiResp {
    String name();//参数名
    String descr();//参数描述
}
