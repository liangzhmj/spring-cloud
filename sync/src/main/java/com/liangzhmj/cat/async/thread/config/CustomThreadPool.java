package com.liangzhmj.cat.async.thread.config;

import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 动态注册线程池bean
 * @author liangzhmj
 */
@Log4j2
//继承动态注册bean的接口,@Autowired或者@Value注解会失效，原因是，spring容器执行接口的方法时，此时还没有去解析@Autowired或者@Value注解。
public class CustomThreadPool implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Environment env;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //spring容器执行接口的方法时，此时还没有去解析@Autowired或者@Value注解。
        //暂时没有找到Environment直接获取对象或者列表的方法，因此只能一个个属性去解析,暂时最多生成20个线程池
        for (int i = 0; i < 20; i++) {
            String name = env.getProperty("async.threadpool.customs["+i+"].name");
            if(StringUtils.isEmpty(name)){//因为name是非空配置，因此如果未空，则可认为，已经读取到末尾了
                log.info("实例化了"+i+"个自定义线程池");
                break;
            }
            /*
            corePoolSize： 核心线程池数量（最小线程数）
            maximumPoolSize：最大线程数
            keepAliveTime： 允许线程空闲时间（单位：默认为秒）
            queueCapacity： 线程池的队列容量
            hanlder: 拒绝策略
             */
            int corePoolSize = env.getProperty("async.threadpool.customs["+i+"].corePoolSize",Integer.class,2);//
            int maxPoolSize = env.getProperty("async.threadpool.customs["+i+"].maxPoolSize",Integer.class,5);
            int keepAliveTime = env.getProperty("async.threadpool.customs["+i+"].keepAliveTime",Integer.class,8);
            int queueCapacity = env.getProperty("async.threadpool.customs["+i+"].queueCapacity",Integer.class,10);
            String handler = env.getProperty("async.threadpool.customs["+i+"].handler",String.class,"blocking");

            //1.构造bean定义builder
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolTaskExecutor.class);
            //2-1:设置依赖(springbean依赖)
//            beanDefinitionBuilder.addPropertyReference("personDao", "personDao");
            //2-2设置属性
            builder.addPropertyValue("corePoolSize", corePoolSize);
            builder.addPropertyValue("maxPoolSize", maxPoolSize);
            builder.addPropertyValue("keepAliveSeconds", keepAliveTime);
            builder.addPropertyValue("queueCapacity", queueCapacity);
            builder.addPropertyValue("threadNamePrefix", name+"-");
            /* 内部自带的4中策略
                AbortPolicy策略：该策略会直接抛出异常，阻止系统正常工作。
                CallerRunsPolicy 策略：只要线程池未关闭，该策略直接在调用者线程中，运行当前的被丢弃的任务。
                DiscardOldestPolicy策略： 该策略将丢弃最老的一个请求，也就是即将被执行的任务，并尝试再次提交当前任务。
                DiscardPolicy策略：该策略默默的丢弃无法处理的任务，不予任何处理。
             */
            RejectedExecutionHandler reh = null;
            if("blocking".equals(handler)){//阻塞策略
                reh = (Runnable r, ThreadPoolExecutor executor)->{
                    if (!executor.isShutdown()) {
                        try {
                            executor.getQueue().put(r);//这里之所以能实现阻塞，是基于BlockingQueue的put方法来实现的(替代原来的offer)，当阻塞队列满时，put方法会一直等待...
                        } catch (InterruptedException e) {
                            log.error(e.toString(), e);
                            Thread.currentThread().interrupt();
                        }
                    }
                };
            }else if("abortPolicy".equals(handler)){
                reh = new ThreadPoolExecutor.AbortPolicy();
            }else if("callerRunsPolicy".equals(handler)){
                reh = new ThreadPoolExecutor.CallerRunsPolicy();
            }else if("discardOldestPolicy".equals(handler)){
                reh = new ThreadPoolExecutor.DiscardOldestPolicy();
            }else if("discardPolicy".equals(handler)){
                reh = new ThreadPoolExecutor.DiscardPolicy();
            }
            if(reh != null){
                builder.addPropertyValue("rejectedExecutionHandler", reh);// 线程池对拒绝任务的处理策略
            }
            //3:初始化
//            builder.setInitMethodName("initialize");//这里不用再显示调用了，spring自动调用
            //注册bean定义
            registry.registerBeanDefinition(name, builder.getBeanDefinition());
            log.info("实例化线程池["+name+"]-{corePoolSize:"+corePoolSize+",maxPoolSize:"+maxPoolSize+",keepAliveTime:"+keepAliveTime+",queueCapacity:"+queueCapacity+"}");
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

