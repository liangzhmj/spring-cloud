package com.liangzhmj.cat.mq.rabbitmq.annotation;


import com.liangzhmj.cat.mq.rabbitmq.config.RabbitmqConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RabbitmqConfig.class})
public @interface EnableRabbitMQProducer {
}
