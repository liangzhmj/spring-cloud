**cat-api-simple模块**
    简介
        该模块是cat-api简洁版，去掉了热部署，动态servlet，动态任务等模块，同时也去掉了对api数据库，api管理后台的依赖。
        该模块包含@Component等一下springBean的注册注解，因此引入该模块，必须要scan到该模块的配置。

​		**@InterAop(order)**: aop类继承APIAspect，请求先经过该类的doPrepare->然后执行业务逻辑->doAfter->doFinally,注意：order不能重复

​		**@InterService(interId)**: 接口类，继承ServiceModule实现业务逻辑

```yml
    api:
        scanPrefix: com.liangzhmj. #注解配置检索的包名前缀，目前要被处理的注解有@InterAop，和@InterService
```

