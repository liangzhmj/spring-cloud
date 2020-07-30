package com.liangzhmj.cat.mq.sql.annotation;


import com.liangzhmj.cat.mq.sql.config.SqlMQConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 实例化一个公共队列,需要自定义的话，可以直接编写一个包含两个队列的类，然后创建消费者，然后调用start()几个启动处理队列，例如：<br/>
 * new SqlConsumer("INSERT-SQL队列", apiDao, 1000, 5, 2,2, "/home/www/wx-tp-api/failSqls/insert/", InsertSqlQ.getQueue1(), InsertSqlQ.getQueue2(), InsertSqlQ.getQueue3()).start();
 * @author liangzhmj
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SqlMQConfig.class})
public @interface EnableSqlMQ {
}
