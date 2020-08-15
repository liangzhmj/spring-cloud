package com.liangzhmj.cat.api.service.annotation;


import java.lang.annotation.*;

/**
 * 用于一个interId，多种业务分支的注解，之前通过method if/else
 * 现在直接@InterMethod(method)就行了
 * ps:
 *  必须是public方法
 *  参数统一是APIReq
 *  返回值是Object
 * @author liangzhmj
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InterMethod {
    String value() ;//APIReq中method的值
}
