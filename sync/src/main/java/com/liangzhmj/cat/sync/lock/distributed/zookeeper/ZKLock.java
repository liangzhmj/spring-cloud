package com.liangzhmj.cat.sync.lock.distributed.zookeeper;

import com.liangzhmj.cat.sync.lock.exception.SyncException;
import lombok.extern.log4j.Log4j2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Log4j2
public class ZKLock {

    private static CuratorFramework curatorFramework;

    public ZKLock(){
        log.warn("实例化ZKLock");
    }

    @Autowired
    @Qualifier("curatorFramework")
    public void setCuratorFramework(CuratorFramework curatorFramework) {
        if(curatorFramework == null){
            throw new SyncException("没有zookeeper-curatorFramework实例，ZKLook依赖cat-tools模块，并且需要在app类上注解EnableZookeeper");
        }
        ZKLock.curatorFramework = curatorFramework;
    }

    /**
     * 获取一个分布式锁，特别注意【每个锁必须release】
     * @param path
     * @return
     */
    public static InterProcessMutex getDLock(String path){
        //推荐每一个线程一个lock实例,因为虽然在同一个客户端里面一个lock也能起到同步锁的作用,但是如果含有监听器,或者请求锁中断的话(重连客户端就变了),就可能出现不同的"误伤"的可能
        InterProcessMutex lock = new InterProcessMutex(curatorFramework, path);
        return lock;
    }

}
