**cat-tools模块**
    简介
        该模块cat的工具类
        注解包括
        **@EnableZookeeper**   #使用Zookeeper


```yml
参考配置
    zk:
        hosts: 119.23.212.182:2181,39.108.70.175:2181
        namespace: cat-demo #zookeeper命名空间（节点），建议一个项目一个
        sessionTimeout: 20000 #会话超时时间，单位毫秒，默认60000ms
        connectionTimeout: 10000 #连接创建超时时间，单位毫秒，默认60000ms
        baseSleepTimeMs: 1000 #失败重连间隔，单位ms
        maxRetries: 5 #最大失败重连连续次数
```