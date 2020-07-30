package com.liangzhmj.cat.mq.action;

import com.liangzhmj.cat.mq.action.queue.ActionQueue;
import com.liangzhmj.cat.mq.action.vo.AbstractAction;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 动作队列管理类
 * @author liangzhmj
 */
@Log4j2
public class ActionMQ {

    private static Map<String,ActionConsumer> consumers = new HashMap<>();
    private static Map<String, ActionQueue> queues = new HashMap<>();

    public static void addMQ(@NonNull String name, @NonNull ActionConsumer consumer, @NonNull ActionQueue queue){
        consumers.put(name,consumer);
        queues.put(name,queue);
    }

    /**
     *  把某个动作action放入到name队列中
     * @param name
     * @param action
     * @return
     */
    public static boolean offer(@NonNull String name, @NonNull AbstractAction action){
        ActionQueue queue = queues.get(name);
        if(queue == null){
            throw new RuntimeException("系统中没到找到["+name+"]的注册动作队列，查找配置mq.actions -name是否配置正确");
        }
        return queue.offer(action);
    }

    /**
     * 根据名称获取2队列元素个数
     * @param name
     * @return
     */
    public static Integer[] queueSize(String name){
        ActionQueue queue = queues.get(name);
        return new Integer[]{queue.getQueue1().size(),queue.getQueue2().size()};
    }

    /**
     * 为了方便，这里定义一个name为common的队列，必须要在配置文件中配置有mq.actions -name: common方可使用
     * @param action
     * @return
     */
    public static boolean offerCommon(@NonNull AbstractAction action){
        return offer("common",action);
    }
    /**
     * 为了方便，这里定义一个name为common的队列，必须要在配置文件中配置有mq.actions -name: common方可使用
     * @return
     */
    public static Integer[] commonQueueSize(){
        return queueSize("common");
    }


    /**
     * 为了方便，这里定义一个name为http的队列，必须要在配置文件中配置有mq.actions -name: http方可使用
     * @param action
     * @return
     */
    public static boolean offerHttp(@NonNull AbstractAction action){
        return offer("http",action);
    }

    /**
     * 为了方便，这里定义一个name为http的队列，必须要在配置文件中配置有mq.actions -name: http方可使用
     * @return
     */
    public static Integer[] httpQueueSize(){
        return queueSize("http");
    }

    /**
     * 获取所有的动作队列消费者线程
     * @return
     */
    public static Collection<ActionConsumer> allConsumers(){
        return consumers.values();
    }

    /**
     * 根据名称获取动作队列消费者线程
     * @param name
     * @return
     */
    public static ActionConsumer getConsumer(@NonNull String name){
        return consumers.get(name);
    }

    public static void stopAllConsumer(){
        Set<Entry<String,ActionConsumer>> ens = consumers.entrySet();
        for (Entry<String, ActionConsumer> en : ens) {
            try {
                ActionConsumer consumer = en.getValue();
                consumer.shutdown();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

}
