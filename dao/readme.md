**cat-dao模块**
    简介
        该模块暂时包括redis，数据库（支持多数据源），ehcache模块，通过注解引入
        注解包括
        @EnableBaseDao   #一般用于基本的mysql数据库，例如一下配置或者控制，参考base数据源指向api管理系统的数据库
        @EnableServiceDao   #一般用于业务的mysql数据库
        @EnableBothDao    #包含@EnableBaseDao,@EnableServiceDao
        @EnableDBConfig    #使用数据库t_properties表的配置（可以选择数据源初始化）,使用DBProperties获取
        @EnableEhcache    #使用ehcache，已经包含@EnableCaching，通过ehcache的注解使用或者通过EhcacheContext存取
        @EnableRedisDao   #使用redisDao

```yml
	#druid监听器提供druid的图形化管理界面
    monitor:
      druid:
        username: root
        password: root
        path: wxapp-data
    #--------------------DBConfig部分-----------start
    dbconfig:
      db: service   #配置表所在数据库
      table: t_properties   #配置表表名
      admin:
        allowips: 127.0.0.1,192.168.1.1   #管理接口服务ip白名单
        #http://ip:port/cache?method=clear&name=rname
        #http://ip:port/cache?method=del&name=rname&key=rkey
        #http://ip:port/cache?method=get&name=rname&key=rkey
    #--------------------DBConfig部分-----------end
    #--------------------ehcache部分-----------start
    cache:
      admin:
        allowips: 127.0.0.1,192.168.1.1   #管理缓存服务ip白名单
        #http://ip:port/dbconfig?method=reload
        #http://ip:port/dbconfig?method=get&key=rkey
    #--------------------ehcache部分-----------end

    #--------------------数据源配置-----------start
    spring:
      datasource:    #mysql配置数据源
        base:   #base源
          url: jdbc:mysql://127.0.0.1:3306/cat-api?characterEncoding=utf8&autoReconnect=true&useSSL=false
          username: root
          password: root
          initialSize: 2
          minIdle: 1
          maxActive: 8
          maxWait: 60000
          driver-class-name: com.mysql.cj.jdbc.Driver
          validation-query: SELECT 1 FROM DUAL
          validation-interval: 600000
          test-while-idle: true
          test-on-borrow: false
          test-on-return: false
          min-evictable-idle-time-millis: 1800000
          time-between-eviction-runs-millis: 1200000
          #filters: stat,wall,log4j    #不设置filters的话web页面监控sql监控不会监控该数据源，换句话说不想监控该数据源的话可以不设置filters
        service:    #service源
          url: jdbc:mysql://127.0.0.1:3306/cat-service?characterEncoding=utf8&autoReconnect=true&useSSL=false
          username: root
          password: root
          driverClassName: com.mysql.cj.jdbc.Driver
          initialSize: 3
          minIdle: 2
          maxActive: 10
          maxWait: 60000
          minEvictableIdleTimeMillis: 1800000
          timeBetweenEvictionRunsMillis: 1200000
          validationQuery: SELECT 1 FROM DUAL
          testWhileIdle: true
          testOnBorrow: false
          testOnReturn: false
          poolPreparedStatements: true
          maxPoolPreparedStatementPerConnectionSize: 100
          filters: stat,wall,log4j2
          connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=8000
      redis:    #redis配置
        #     cluster:
        #       nodes:
        #        - 192.168.1.100:6379
        #        - 192.168.1.101:6379
        #        - 192.168.1.102:6379
        host: 127.0.0.1
        port: 6379
        database: 0
        timeout: 60s  # 数据库连接超时时间，2.0 中该参数的类型为Duration，这里在配置的时候需要指明单位连接池配置，2.0中直接使用jedis或者lettuce配置连接池
        lettuce:
          pool:
            # 最大空闲连接数
            max-idle: 50
            # 最小空闲连接数
            min-idle: 5
            # 等待可用连接的最大时间，负数为不限制
            max-wait:  -1s
            # 最大活跃连接数，负数为不限制
            max-active: -1
    #--------------------数据源配置-----------end
```