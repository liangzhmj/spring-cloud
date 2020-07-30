package com.liangzhmj.cat.tools.json;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 注解要添加到json的属性
 * @author liangzhmj
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONField {
	String name() default "";
	String clazz() default "";
}
