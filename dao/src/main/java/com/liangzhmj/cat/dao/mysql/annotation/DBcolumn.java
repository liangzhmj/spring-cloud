package com.liangzhmj.cat.dao.mysql.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 注解要添加到实体类的属性
 * @author liangzhmj
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DBcolumn {
	String name() default "";
	boolean pk() default false;
}
