**使用说明**

- **cat-api**

  api模块，推荐post-body-json的方式传入数据，支持get?params=URIEncoder.encode({参数})。

  接口路径：/interfaceAction	//对于所有的api路径是一样的

  例子参数: {interId:"100001",version:1,params:{size:1,page:15}} //其中100001是接口编号，不t同的api对应不用的业务类，可以添加method参数，在业务类里面实现不用的逻辑。

  该模块，通过注解使用组件，特色部分有：

  1.实现业务类，aop，任务调度，热加载，实现一次部署，基本不用停机。90%以上的更新可以通过后台操作。

  2.动态接口，通过配置和支持类的动态部署，实现系统动态分配api供外部调用，主要用于一些合作方的数据同步，例如支付结果通知

- **cat-api-simple**

  api-simple模块是api模块的阉割版，协议没变，主要去掉了热部署,动态接口部分。这样做的好处是摆脱了该模块对后台和配置数据库的依赖，实现注解api，适合小项目开始开发。

  在访问服务类的前后。

  参考目录

  ![image-20200730185043600](C:\Users\liangzhmj\AppData\Roaming\Typora\typora-user-images\image-20200730185043600.png)

  该系统对请求进行了aop拦截，配置aop如下

  ![image-20200730184419646](C:\Users\liangzhmj\AppData\Roaming\Typora\typora-user-images\image-20200730184419646.png)

  业务类配置如下

  ![image-20200730184948985](C:\Users\liangzhmj\AppData\Roaming\Typora\typora-user-images\image-20200730184948985.png)

- **cat-dao**

- **cat-mq**

- **cat-sync**

- **cat-sokect**

- **cat-tools**

- **cat-ext-wechat**

​	