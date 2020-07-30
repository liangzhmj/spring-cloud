package com.liangzhmj.cat.mq.action.annotation;


import com.liangzhmj.cat.mq.action.config.ActionMQConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 实例化一个公共队列,需要自定义的话，可以直接编写一个包含两个队列的类，然后创建消费者，然后调用start()几个启动处理队列，例如：<br/>
 * new ActionConsumer("COMMON-ACTION队列", 5, CommonActionQ.getQueue1(), CommonActionQ.getQueue2()),start();
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ActionMQConfig.class})
public @interface EnableActionMQ {
}
