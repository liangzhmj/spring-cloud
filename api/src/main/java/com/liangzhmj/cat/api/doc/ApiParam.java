package com.liangzhmj.cat.api.doc;


import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiParam {
    String[] method() default {};//参数对应的method,应用ApiReq.method,没有的表示该接口没有区分方法。每个参数中所有method数组的并集都是接口的一个method
    ParamLevel level() default ParamLevel.PARAMS;//参数层级,默认params级
    String name();//参数名
    String descr();//参数描述
    ParamDataType dataType() default ParamDataType.STRING;//数据类型
    boolean required() default false;//是否必填
}
