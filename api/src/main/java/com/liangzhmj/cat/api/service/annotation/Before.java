package com.liangzhmj.cat.api.service.annotation;


import java.lang.annotation.*;

/**
 * 必须与InterMethod搭配使用
 * 预处理，不同的method分支可能需要一些预处理，例如某些参数的校验（可能是多种逻辑的组合）
 * 因此每一种逻辑对应一个注解，这样可以灵活处理
 * ps:
 *  必须是public方法
 *  参数统一是APIReq
 *  返回值是APIReq
 * @author liangzhmj
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Before {
    String[] value() ;//后处理参数的名称，指定的方法必须是当前类或者是继承父类/接口的public的方法
}
