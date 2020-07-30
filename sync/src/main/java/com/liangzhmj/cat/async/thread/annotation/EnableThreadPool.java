package com.liangzhmj.cat.async.thread.annotation;


import com.liangzhmj.cat.async.thread.config.CustomThreadPool;
import com.liangzhmj.cat.async.thread.config.DefaultThreadPool;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.lang.annotation.*;

/**
 * 实例化线程池，通过@async("线程池名称")指定线程池线程运行逻辑
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableAsync
@Import({DefaultThreadPool.class,CustomThreadPool.class})
public @interface EnableThreadPool {
}
