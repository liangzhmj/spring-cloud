package com.liangzhmj.cat.api.doc;


import java.lang.annotation.*;

/**
 * 空参数方法，由于接口文档是通过@ApiParam的method获取method的值，如果有些method没有私有参数的话，可以通过该注解，添加到文档中
 * @liangzhmj
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiEmptyMethod {
    String[] value() ;
}
