package com.liangzhmj.cat.api.service.annotation;


import java.lang.annotation.*;

/**
 * 必须与InterMethod搭配使用
 * 后处理，不同的method分支可能需要一些后处理，例如签名，水印，header设置等等（可能是多种逻辑的组合）
 * 因此每一种逻辑对应一个注解，这样可以灵活处理
 * ps:
 *  必须是public方法
 *  参数统一是APIReq,Object(前面处理的结果)
 *  返回值是Object
 * @author liangzhmj
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface After {
    String[] value() ;//后处理参数的名称，指定的方法必须是当前类或者是继承父类/接口的public的方法
}
