package com.liangzhmj.cat.mq.rabbitmq.config;

import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Collections;

/**
 * 动态注册Rabbitmq生产者配置
 * @author liangzhmj
 */
@Log4j2
//继承动态注册bean的接口,@Autowired或者@Value注解会失效，原因是，spring容器执行接口的方法时，此时还没有去解析@Autowired或者@Value注解。
public class RabbitmqConfig implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Environment env;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //spring容器执行接口的方法时，此时还没有去解析@Autowired或者@Value注解。
        //暂时没有找到Environment直接获取对象或者列表的方法，因此只能一个个属性去解析,暂时最多生成20个线程池
        for (int i = 0; i < 20; i++) {
            String queue = env.getProperty("mq.rabbits["+i+"].queue");
            if(StringUtils.isEmpty(queue)){//因为queue是非空配置，因此如果为空，则可认为，已经读取到末尾了
                log.info("实例化了"+i+"个rabbitmq生产者");
                break;
            }
            String[] qs = queue.split(",");//非direct
            boolean durable = env.getProperty("mq.rabbits["+i+"].durable",Boolean.class,false);
            String exchage = env.getProperty("mq.rabbits["+i+"].exchange",String.class,null);
            String exchangeType = env.getProperty("mq.rabbits["+i+"].exchangeType",String.class,"direct");
            String routingKey = env.getProperty("mq.rabbits["+i+"].routingKey",String.class,"");
            String[] rks = routingKey.split(",");
            if(exchangeType.equals("topic") && qs.length != rks.length){
                log.error("队列与路由key数量不匹配:["+queue+"]-["+routingKey+"]");
                continue;
            }
            if(exchangeType.equals("direct") && qs.length > 1){
                log.error("direct模式不支持多队列:"+queue);
                continue;
            }
            //1.创建队列(可能是多个)
            for (String q : qs) {
                BeanDefinitionBuilder queueBuilder = BeanDefinitionBuilder.genericBeanDefinition(Queue.class);
                //设置构造函数参数值Queue(String name, boolean durable)
                queueBuilder.addConstructorArgValue(q).addConstructorArgValue(durable);
                //注册bean定义
                registry.registerBeanDefinition(q, queueBuilder.getBeanDefinition());
            }
            if(StringUtils.isEmpty(exchage)){
                continue;
            }
            BeanDefinitionBuilder exchangeBuilder;
            //2.创建交换机
            if("topic".equals(exchangeType)){
                exchangeBuilder =BeanDefinitionBuilder.genericBeanDefinition(TopicExchange.class);
            }else if("fanout".equals(exchangeType)){
                exchangeBuilder =BeanDefinitionBuilder.genericBeanDefinition(FanoutExchange.class);
            }else {
                exchangeBuilder =BeanDefinitionBuilder.genericBeanDefinition(DirectExchange.class);
            }
            //设置构造函数参数值DirectExchange(String name)
            exchangeBuilder.addConstructorArgValue(exchage);
            //注册bean定义
            registry.registerBeanDefinition(exchage, exchangeBuilder.getBeanDefinition());
            //3.绑定交换机
            for (int j = 0; j < qs.length; j++) {
                String rk = qs.length != rks.length?rks[0]:rks[j];
                BeanDefinitionBuilder bindBuilder = BeanDefinitionBuilder.genericBeanDefinition(Binding.class);
                //Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, @Nullable Map<String, Object> arguments)
                bindBuilder.addConstructorArgValue(qs[j])
                        .addConstructorArgValue(Binding.DestinationType.QUEUE)
                        .addConstructorArgValue(exchage)
                        .addConstructorArgValue(rk)
                        .addConstructorArgValue(Collections.emptyMap());
                //注册bean定义
                registry.registerBeanDefinition(qs[j]+'-'+rk, bindBuilder.getBeanDefinition());
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}

