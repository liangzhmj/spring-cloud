package com.liangzhmj.cat.sync.lock.distributed.zookeeper.annotation;


import com.liangzhmj.cat.sync.lock.distributed.zookeeper.ZKLock;
import com.liangzhmj.cat.sync.lock.distributed.zookeeper.aop.DLockAop;
import com.liangzhmj.cat.tools.zookeeper.annotation.EnableZookeeper;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ZKLock.class, DLockAop.class})
@EnableZookeeper
public @interface EnableZKLock {
}
