package com.liangzhmj.cat.mq.sql;

import com.liangzhmj.cat.mq.exception.MqException;
import com.liangzhmj.cat.mq.sql.queue.SqlQueue;
import com.liangzhmj.cat.tools.string.StringUtils;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 动作队列管理类
 * @author liangzhmj
 */
@Log4j2
public class SqlMQ {

    private static Map<String,SqlConsumer> consumers = new HashMap<>();
    private static Map<String, SqlQueue> queues = new HashMap<>();

    public static void addMQ(@NonNull String name, @NonNull SqlConsumer consumer, @NonNull SqlQueue queue){
        consumers.put(name,consumer);
        queues.put(name,queue);
    }

    /**
     *  把sql放入到name队列中
     * @param name
     * @param sql
     * @return
     */
    public static boolean offer(@NonNull String name, String sql){
        if(StringUtils.isEmpty(sql)){
            return false;
        }
        SqlQueue queue = queues.get(name);
        if(queue == null){
            throw new MqException("系统中没到找到["+name+"]的注册Sql队列，查找配置mq.sqls -name是否配置正确");
        }
        return queue.offer(sql);
    }

    /**
     * 根据名称获取3队列元素个数
     * @param name
     * @return
     */
    public static Integer[] queueSize(String name){
        SqlQueue queue = queues.get(name);
        return new Integer[]{queue.getQueue1().size(),queue.getQueue2().size(),queue.getQueue3().size()};
    }

    /**
     * 为了方便，这里定义一个name为common的队列，必须要在配置文件中配置有mq.sqls -name: common方可使用
     * @param sql
     * @return
     */
    public static boolean offerCommon(String sql){
        if(StringUtils.isEmpty(sql)){
            return false;
        }
        return offer("common",sql);
    }

    /**
     * 为了方便，这里定义一个name为common的队列，必须要在配置文件中配置有mq.sqls -name: common方可使用
     * @return
     */
    public static Integer[] commonQueueSize(){
        return queueSize("common");
    }

    /**
     * 为了方便，这里定义一个name为insert的队列，必须要在配置文件中配置有mq.sqls -name: insert方可使用
     * @param sql
     * @return
     */
    public static boolean offerInsert(String sql){
        if(StringUtils.isEmpty(sql)){
            return false;
        }
        return offer("insert",sql);
    }

    /**
     * 为了方便，这里定义一个name为insert的队列，必须要在配置文件中配置有mq.sqls -name: insert方可使用
     * @return
     */
    public static Integer[] insertQueueSize(){
        return queueSize("insert");
    }

    /**
     * 为了方便，这里定义一个name为update的队列，必须要在配置文件中配置有mq.sqls -name: update方可使用
     * @param sql
     * @return
     */
    public static boolean offerUpdate(String sql){
        if(StringUtils.isEmpty(sql)){
            return false;
        }
        return offer("update",sql);
    }

    /**
     * 了方便，这里定义一个name为update的队列，必须要在配置文件中配置有mq.sqls -name: update方可使用
     * @return
     */
    public static Integer[] updateQueueSize(){
        return queueSize("update");
    }

    /**
     * 为了方便，这里定义一个name为delete的队列，必须要在配置文件中配置有mq.sqls -name: delete方可使用
     * @param sql
     * @return
     */
    public static boolean offerDelete(String sql){
        if(StringUtils.isEmpty(sql)){
            return false;
        }
        return offer("delete",sql);
    }

    /**
     * 为了方便，这里定义一个name为delete的队列，必须要在配置文件中配置有mq.sqls -name: delete方可使用
     * @return
     */
    public static Integer[] delQueueSize(){
        return queueSize("delete");
    }



    /**
     * 根据名称获取动作队列消费者线程
     * @param name
     * @return
     */
    public static SqlConsumer getConsumer(@NonNull String name){
        return consumers.get(name);
    }

    public static void stopAllConsumer(){
        Set<Entry<String,SqlConsumer>> ens = consumers.entrySet();
        for (Entry<String, SqlConsumer> en : ens) {
            try {
                SqlConsumer consumer = en.getValue();
                consumer.shutdown();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

}
