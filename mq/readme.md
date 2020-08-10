**cat-mq模块**
    简介
        该模块暂时包括处理sql队列，处理action队列模块，通过注解引入
        注解包括
        **@EnableActionMQ**   #动作队列ActionMQ.offer(@NonNull String name, AbstractAction action)对入列action进行处理,name为队列名，要在配置文件中配置mq.actions -name: name
                          #系统默认生成了offerCommon，offerHttp,必须要配置对应的name例如：mq.action -name: [http]才会生效
        **@EnableSqlMQ**      #sql队列,通过SqlMQ.offer(@NonNull String name, @NonNull String sql)对入列sql进行处理,name为队列名，要在配置文件中配置mq.sqls -name: name
          系统默认生成了offerCommon，offerInsert,offerUpdate,offerDelete,必须要配置对应的name例如：mq.sqls -name: [delete]才会生效

​		**@EnableRabbitMQProducer** #rabbitmq队列，可以动态配置交换机，队列，路由key等信息

```yml
    #cat-mq配置
    mq:
      actions:  #动作队列
        - name: common  #队列名称
          pool: 2  #执行线程池数量
          interval: 300  #读取队列最大等待时间间隔
        - name: http
          pool: 5
          interval: 50
      sqls:  #sql队列
        - name: common #队列名称
          db: service   #目标数据库，cat-dao模块中的base，service
          max: 1000  #一次性处理最大sql数
          pool1: 3  #第一轮线程池数量
          pool2: 2  #第二轮线程池数量
          interval: 300  #读取队列最大等待时间间隔
          failFilePath: E:/failSqls/common/  #失败sql保存文件夹
        - name: insert
          db: service
          max: 1000
          pool1: 5
          pool2: 2
          interval: 200
          failFilePath: E:/failSqls/insert/
        - name: update
          db: service
          max: 1000
          pool1: 6
          pool2: 3
          interval: 200
          failFilePath: E:/failSqls/update/
        - name: delete
          db: service
          max: 1000
          pool1: 5
          pool2: 2
          interval: 200
          failFilePath: E:/failSqls/delete/
      rabbits:
        - queue: cat-q1,cat-q2,cat-q4	#队列，direct(只能有一个)，topic和fanout可以多个
          durable: true	#队列是否持久化
          exchange: cat-topic	#交换机名称
          exchangeType: topic	#交换机类型
          routingKey: sql.#,action.#,mq.*	#路由key，topic模式，这里必须要和queue的数量对应
        - queue: cat-q3
          durable: true
          exchange: cat-ex3
          exchangeType: direct
          routingKey: rkey3
```

**注意**

​	该模块sqlMQ部分使用cat-dao模块，如果不使用sqlMQ的话，默认引入也是需要数据库连接配置和@EnablexxxDao的，所以如果不用sqlMQ的话，可以在引导类那里修改注解

```java
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
```