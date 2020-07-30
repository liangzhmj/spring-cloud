**cat-sync模块**
    简介
        该模块暂时包括同步异步模块，通过注解引入
        注解包括
        **@EnableZKLock**   #使用Zookeeper分布式锁，依赖已包含@EnableZookeeper（cat-tools模块）
        **@DLock**          #分布式锁，注解在方法上面，对方法进行分布式处理
        **@EnableThreadPool** #使用异步连接池通过@Async(池名)使用，也可以裸用，例如@Autowrite(池名) priviate Executor executor


```yml
    async:
      threadpool:
        default:  #默认线程池
          name: my-default-thread-  #池名
          corePoolSize: 2 #核心线程池数量（最小线程数）
          maxPoolSize: 5  #最大线程数
          keepAliveTime: 10   #允许线程空闲时间（单位：默认为秒）
          queueCapacity: 10 #线程池的队列容量
          #拒绝策略，默认没有，blocking(阻塞),abortPolicy,callerRunsPolicy,discardOldestPolicy,discardPolicy
          hanlder: blocking 
        customs:  #自定义线程池数组，最多配置20个
          - name: mypool-2  #池名
            corePoolSize: 2
            maxPoolSize: 5
            keepAliveTime: 8
            queueCapacity: 10
          - name: mypool-10 #池名
            corePoolSize: 10
            maxPoolSize: 100
            keepAliveTime: 20
            queueCapacity: 200

    -->tools的zookeeper配置
        zk:
            hosts: 119.23.212.182:2181,39.108.70.175:2181
            namespace: cat-demo #zookeeper命名空间（节点），建议一个项目一个
            sessionTimeout: 20000 #会话超时时间，单位毫秒，默认60000ms
            connectionTimeout: 10000 #连接创建超时时间，单位毫秒，默认60000ms
            baseSleepTimeMs: 1000 #失败重连间隔，单位ms
            maxRetries: 5 #最大失败重连连续次数
```
