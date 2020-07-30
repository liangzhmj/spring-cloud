package com.liangzhmj.cat.async.thread.config;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Setter
@Log4j2
@ConfigurationProperties(prefix = "async.threadpool.default")  //这里报错不用管，应该是说@ConfigurationProperties要在bean类上，这里通过@Import注入，而编辑器并不知道
public class DefaultThreadPool implements AsyncConfigurer{//默认线程池（@Async不加参数）

    private int corePoolSize = 5;       		// 核心线程数（最小线程数）
    private int maxPoolSize = 100;			    // 最大线程数
    private int keepAliveTime = 10;			// 允许线程空闲时间（单位：默认为秒）
    private int queueCapacity  = 200;			// 缓冲队列数
    private String name = "my-default-thread-"; // 线程池名前缀

    @Override
    public Executor getAsyncExecutor() {
        /**
         * 如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。
         * 如果此时线程池中的数量等于 corePoolSize，但是缓冲队列 workQueue未满，那么任务被放入缓冲队列。
         * 如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maxPoolSize，建新的线程来处理被添加的任务。
         * 如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maxPoolSize，那么通过handler所指定的策略来处理此任务。也就是：处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程 maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。
         * 当线程池中的线程数量大于corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。
         */
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池数量，方法: 返回可用处理器的Java虚拟机的数量。
        executor.setCorePoolSize(corePoolSize);
        //最大线程数量
        executor.setMaxPoolSize(maxPoolSize);
        //线程池的队列容量
        executor.setQueueCapacity(queueCapacity);
        // 允许线程空闲时间（单位：默认为秒）
        executor.setKeepAliveSeconds(keepAliveTime);
        //线程名称的前缀
        executor.setThreadNamePrefix(name);
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        //executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    /*异步任务中异常处理*/
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        // TODO Auto-generated method stub
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}

