package com.liangzhmj.cat.sync.lock.distributed.zookeeper.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DLock {
    long ms() default 0;
    String value() ;
}
