package com.liangzhmj.cat.mq.action.config;

import com.liangzhmj.cat.mq.action.ActionConsumer;
import com.liangzhmj.cat.mq.action.ActionMQ;
import com.liangzhmj.cat.mq.action.queue.ActionQueue;
import com.liangzhmj.cat.tools.collection.CollectionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 实例化一个队列,需要自定义的话，可以直接编写一个包含两个队列的类，然后创建消费者，然后调用start()启动处理队列，例如：<br/>
 * new ActionConsumer("COMMON-ACTION队列", 5, CommonActionQ.getQueue1(), CommonActionQ.getQueue2()),start();
 */
@Log4j2
@Setter
@Getter
@ConfigurationProperties(prefix = "mq")//这里报错不用管，应该是说@ConfigurationProperties要在bean类上，这里通过@Import注入，而编辑并不知道
public class ActionMQConfig {

	private List<Config> actions;

	@PostConstruct
	public void registyActionMq(){
		if(CollectionUtils.isEmpty(actions)){
			log.error("配置中找不到mq.actions配置信息，请在application.yml中配置mq.actions - [name,pool,interval]");
			return;
		}
		for (Config action : actions) {
			try {
				ActionQueue aq = new ActionQueue();
				ActionConsumer ac = new ActionConsumer(action.getName(), action.getPool(),action.getInterval(), aq.getQueue1(), aq.getQueue2());
				ac.start();
				ActionMQ.addMQ(action.getName(),ac,aq);
				log.info("队列["+action.getName()+"]已注册-{pool:"+action.getPool()+",interval:"+action.getInterval()+"}");
			} catch (Exception e) {
				log.error("部分动作队列初始化失败:"+action,e);
			}
		}
	}

}