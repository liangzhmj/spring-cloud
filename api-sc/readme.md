**cat-api-sc模块**
    简介

​        与cat-api不一样的是springmvc的入口

​		该模块包含api传输协议，api热部署，动态servlet，动态任务,在线文档等。
​        该模块包含@Component等一下springBean的注册注解，因此引入该模块，必须要scan到该模块的配置。
​        注解包括
​        **@EnableSynaServlet**  #使用动态Servlet

```yml
参考配置
    api:
      projectId: 1  #项目id（后台api管理系统中project的自增ID，在系统创建的时候会分配）
      aop:
        regexp: ^com\\.wx\\.tp\\.hotload\\.auxiliary\\.aop\\..+Aspect$  #热部署的aop切面链正则（暂时只支持一个，可以用来处理权限，记录日志等操作）
      job:
        regexp: ^com\\.wx\\.tp\\.hotload\\.auxiliary\\.job\\.J.+$   #热部署动态job处理类正则
      syncServlet:  #通过APIConfigUtils.genervalSynaServlets()初始化动态servlet,只运行一次
        start: 261  #动态Servlet-url的后缀开始index，需要api管理系统获取
        count: 20   #实例动态Servlet的个数
      admin:
        manager:
          ips: 127.0.0.1;192.168.1.1  #暴露给管理后台的ip白名单
```
