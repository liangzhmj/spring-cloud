**使用说明**

- **cat-api**

  api模块，推荐post-body-json的方式传入数据，支持get?params=URIEncoder.encode({参数})。

  接口路径：/interfaceAction	//对于所有的api路径是一样的

  例子参数: {interId:"100001",version:1,params:{size:1,page:15}} //其中100001是接口编号,params是json形式的扩展参数，不同的api对应不用的业务类，可以添加method参数，在业务类里面实现不用的逻辑。

  该模块，通过注解使用组件，特色部分有：

  1.实现业务类，aop，任务调度，热加载，实现一次部署，基本不用停机。90%以上的更新可以通过后台操作。

  2.动态接口，通过配置和支持类的动态部署，实现系统动态分配api供外部调用，主要用于一些合作方的数据同步，例如支付结果通知

- **cat-api-simple(依赖cat-dao，cat-tools)**

  api-simple模块是api模块的阉割版，协议没变，主要去掉了热部署,动态接口部分。这样做的好处是摆脱了该模块对后台和配置数据库的依赖，实现注解api，适合小项目开始开发。

  在访问服务类的前后,该系统对请求进行了aop拦截，配置aop如下

  ```java
  @Log4j2
  @InterAop(1)//注解接口,1表示顺序，支持多切面配置
  public class CommonAspect implements APIAspect {
     @Override
     public APIReq doPrepare(APIReq req)  throws Exception{
        log.info("切面-common-prepare---");
        if("10001".equals(req.getInterId())){
           return req;
        }
        WXSession session = WXSessionUtils.tryGetSession(req.getAuthKey());
        req.setSession(session);
        return req;
     }
     @Override
     public Object doAfter(APIReq req, Object data) throws Exception {
        log.info("切面-common-after---");
        return data;
     }
     @Override
     public Result doFinally(APIReq req, Result res) throws Exception {
        log.info("切面-common-finally---");
        return res;
     }
  }
  ```

  业务类配置如下

  ```java
  @Log4j2
  @InterService("10001")//注解业务类，10001表示接口编号，与api协议里面的interId相同
  					 //通过不同的interId调用不同的业务逻辑
  public class WXLogin implements ServiceModule {
  	private APIDao baseDao = DaoBeanUtils.getBaseDao();
  	@Override
  	public JSONObject doService(APIReq req) throws Exception {
  		JSONObject result = new JSONObject();
  		//这里写你的业务，有异常，或者自抛的异常，放心往外抛，外部统一做了异常处理
  		//这里的result，就是用户看到的结果了，默认code是状态key，code可以不加，如果没有的话
  		//外层可以帮你加上
  		return result;
  	}
  }
  ```

  

- **cat-dao**

  该模块暂时包括redis，数据库（支持多数据源），ehcache模块，通过注解引入
          注解包括
          @EnableBaseDao   #一般用于基本的mysql数据库，例如一下配置或者控制，参考base数据源指向api管理系统的数据库
          @EnableServiceDao   #一般用于业务的mysql数据库
          @EnableBothDao    #包含@EnableBaseDao,@EnableServiceDao
          @EnableDBConfig    #使用数据库t_properties表的配置（可以选择数据源初始化）,使用DBProperties获取
          @EnableEhcache    #使用ehcache，已经包含@EnableCaching，通过ehcache的注解使用或者通过EhcacheContext存取
          @EnableRedisDao   #使用redisDao

- **cat-mq**

  该模块暂时包括处理sql队列，处理action队列模块，通过注解引入
          注解包括
          @EnableActionMQ   

  ​				1.动作队列ActionMQ.offer(@NonNull String name, AbstractAction action)对入列action进行处理,name为队列名，要在配置文件中配置mq.actions -name: name
  ​                 2.系统默认生成了offerCommon，offerHttp,必须要配置对应的name例如：mq.action -name: [http]才会生效
  ​        @EnableSqlMQ      

  ​				 1.sql队列,通过SqlMQ.offer(@NonNull String name, @NonNull String sql)对入列sql进行处理,name为队列名，要在配置文件中配置mq.sqls -name: name
  ​          		2.系统默认生成了offerCommon，offerInsert,offerUpdate,offerDelete,必须要配置对应的name例如：mq.sqls -name: [delete]才会生效

- **cat-sync**

  该模块暂时包括同步异步模块，通过注解引入
          注解包括
          @EnableZKLock   #使用Zookeeper分布式锁，依赖已包含@EnableZookeeper（cat-tools模块）
          @DLock          #分布式锁，注解在方法上面，对方法进行分布式处理
          @EnableThreadPool	#使用异步连接池通过@Async(池名)使用，也可以裸用，例如@Autowrite(池名) priviate Executor executor

- **cat-sokect**

  该模块暂时包括netty模块，通过注解引入
          注解包括
          @EnableNettyServer   #在配置文件中指定相关参数（特别是handler），通过handler中的回调方法进行业务处理,ServerChannelContexts工具类对对应客户端ctx进程存储
          @EnableNettyClient   #在配置文件中指定相关参数（特别是handler），通过handler中的回调方法进行业务处理,ClientChannelContexts工具类对对应服务端ctx进程存储

- **cat-tools**

  工具类集合，包含@EnableZookeeper

- **cat-ext-wechat**

  微信开放第三方平台+微信公众平台接口封装，热插拔，直接调用

​	